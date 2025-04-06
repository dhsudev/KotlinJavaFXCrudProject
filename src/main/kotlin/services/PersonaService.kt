package services.persona

import Database
import models.*
import java.sql.*

class PersonaService {

    fun insertPersona(
        dni: Int,
        name: String,
        surname1: String,
        surname2: String,
        birthDate: Date,
        phone: String,
        email: String?,
        role: String,
        additionalData: Map<String, Any>
    ) {
        val sql =
            "INSERT INTO PERSONA (dni, nom, cognom1, cognom2, data_naix, telefon, mail) VALUES (?, ?, ?, ?, ?, ?, ?)"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.setString(2, name)
                it.setString(3, surname1)
                it.setString(4, surname2)
                it.setDate(5, birthDate)
                it.setString(6, phone)
                it.setString(7, email)
                it.executeUpdate()
            }
        }

        when (role) {
            Role.DOCTOR_EN -> insertDoctor(dni, additionalData["especialitat"] as String)
            Role.PATIENT_EN -> insertPatient(dni, additionalData["nss"] as String, additionalData["genere"] as Char)
            Role.TECHNICIAN_EN -> insertTechnician(dni, additionalData["data_inici"] as Date)
        }
    }

    fun insertDoctor(dni: Int, especialitat: String) {
        val sql = "INSERT INTO METGE (dni_metge, especialitat) VALUES (?, ?)"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.setString(2, especialitat)
                it.executeUpdate()
            }
        }
    }

    fun insertPatient(dni: Int, nss: String, genere: Char) {
        val sql = "INSERT INTO PACIENT (dni_pacient, nss, genere) VALUES (?, ?, ?)"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.setString(2, nss)
                it.setString(3, genere.toString())
                it.executeUpdate()
            }
        }
    }

    fun insertTechnician(dni: Int, dataInici: Date) {
        val sql = "INSERT INTO TECNIC (dni_tecnic, data_inici) VALUES (?, ?)"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.setDate(2, dataInici)
                it.executeUpdate()
            }
        }
    }

    fun updatePersona(
        dni: Int,
        name: String?,
        surname1: String?,
        surname2: String?,
        birthDate: Date?,
        phone: String?,
        email: String?,
        role: String
    ) {
        val sql = """
            UPDATE PERSONA SET 
                nom = COALESCE(?, nom), 
                cognom1 = COALESCE(?, cognom1), 
                cognom2 = COALESCE(?, cognom2), 
                data_naix = COALESCE(?, data_naix), 
                telefon = COALESCE(?, telefon), 
                mail = COALESCE(?, mail) 
            WHERE dni = ?
        """
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.setString(2, surname1)
                stmt.setString(3, surname2)
                stmt.setDate(4, birthDate)
                stmt.setString(5, phone)
                stmt.setString(6, email)
                stmt.setInt(7, dni)
                stmt.executeUpdate()
            }
        }
    }

    fun deletePersona(dni: Int) {
        val sql = "DELETE FROM PERSONA WHERE dni = ?"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, dni)
                stmt.executeUpdate()
            }
        }
    }

    fun listPersonas(offset: Int, role: String): List<Persona> {
        val sql = buildSqlQuery(role)
        val personas = mutableListOf<Persona>()
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, offset)
                val rs = stmt.executeQuery()
                while (rs.next()) {
                    val persona = createPersonaFromResultSet(rs, role)
                    val specificPersona = if (role == Role.NONE_CA) {
                        getSpecificPersona(conn, persona)
                    } else {
                        createSpecificPersona(rs, persona, role)
                    }
                    personas.add(specificPersona)
                }
            }
        }
        return personas
    }

    fun buildSqlQuery(role: String): String {
        val roleTable = when (role) {
            Role.DOCTOR_CA -> "METGE"
            Role.PATIENT_CA -> "PACIENT"
            Role.TECHNICIAN_CA -> "TECNIC"
            else -> null
        }

        val roleColumn = when (role) {
            Role.DOCTOR_CA -> "dni_metge"
            Role.PATIENT_CA -> "dni_pacient"
            Role.TECHNICIAN_CA -> "dni_tecnic"
            else -> "dni"
        }

        return if (roleTable != null) {
            """
            SELECT p.dni as dni, p.nom, p.cognom1, p.cognom2, p.data_naix, p.telefon, p.mail, r.*
            FROM PERSONA p
            JOIN $roleTable r ON p.dni = r.$roleColumn
            LIMIT 10 OFFSET ?
            """
        } else {
            """
            SELECT p.dni as dni, p.nom, p.cognom1, p.cognom2, p.data_naix, p.telefon, p.mail
            FROM PERSONA p
            LIMIT 10 OFFSET ?
            """
        }
    }

    fun createPersonaFromResultSet(rs: ResultSet, role: String): Persona {
        return Persona(
            rs.getInt("dni"),
            rs.getString("nom"),
            rs.getString("cognom1"),
            rs.getString("cognom2"),
            rs.getDate("data_naix"),
            rs.getString("telefon"),
            rs.getString("mail"),
            role
        )
    }

    fun getSpecificPersona(conn: Connection, persona: Persona): Persona {
        val roleCheckSql = """
        SELECT 'DOCTOR' as role, especialitat::text, NULL::text as nss, NULL::text as genere, NULL::text as data_inici FROM METGE WHERE dni_metge = ?
        UNION
        SELECT 'PACIENT' as role, NULL::text as especialitat, nss::text, genere::text, NULL::text as data_inici FROM PACIENT WHERE dni_pacient = ?
        UNION
        SELECT 'TECNIC' as role, NULL::text as especialitat, NULL::text as nss, NULL::text as genere, data_inici::text FROM TECNIC WHERE dni_tecnic = ?
    """
        conn.prepareStatement(roleCheckSql).use { roleStmt ->
            roleStmt.setInt(1, persona.dni)
            roleStmt.setInt(2, persona.dni)
            roleStmt.setInt(3, persona.dni)
            val roleRs = roleStmt.executeQuery()
            if (roleRs.next()) {
                return when (roleRs.getString("role")) {
                    "DOCTOR" -> Doctor(roleRs.getString("especialitat"), persona)
                    "PACIENT" -> Patient(roleRs.getString("nss"), roleRs.getString("genere")[0], persona)
                    "TECNIC" -> Technician(Date.valueOf(roleRs.getString("data_inici")), persona)
                    else -> persona
                }
            }
        }
        return persona
    }

    fun createSpecificPersona(rs: ResultSet, persona: Persona, role: String): Persona {
        return when (role) {
            Role.DOCTOR_CA -> Doctor(rs.getString("especialitat"), persona)
            Role.PATIENT_CA -> Patient(rs.getString("nss"), rs.getString("genere")[0], persona)
            Role.TECHNICIAN_CA -> Technician(rs.getDate("data_inici"), persona)
            else -> persona
        }
    }

    fun getRegistersCount(role: String) : Int{
        val sql = if (role == Role.NONE_CA) {
            "SELECT COUNT(*) FROM PERSONA"
        } else {
            val roleTable = when (role) {
                Role.DOCTOR_CA -> "METGE"
                Role.PATIENT_CA -> "PACIENT"
                Role.TECHNICIAN_CA -> "TECNIC"
                else -> Role.NONE_CA
            }
            val roleColumn = when (role) {
                Role.DOCTOR_CA -> "dni_metge"
                Role.PATIENT_CA -> "dni_pacient"
                Role.TECHNICIAN_CA -> "dni_tecnic"
                else -> "dni"
            }
            """
        SELECT COUNT(*)
        FROM PERSONA p
        JOIN $roleTable r ON p.dni = r.$roleColumn
        """
        }

        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return rs.getInt(1)
                }
            }
        }
        return 0
    }

}