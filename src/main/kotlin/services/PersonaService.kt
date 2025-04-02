package services.persona

import Database
import models.Role
import java.sql.*

class PersonaService {

    fun insertPersona(dni: Int, name: String, surname1: String, surname2: String, birthDate: Date, phone: String, email: String?, role: String) {
        val sql = "INSERT INTO PERSONA (dni, nom, cognom1, cognom2, data_naix, telefon, mail) VALUES (?, ?, ?, ?, ?, ?, ?)"
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
            when (role) {
                Role.DOCTOR -> insertDoctor(dni)
                Role.TECHNICIAN -> insertTechnician(dni)
                Role.PATIENT -> insertPatient(dni)
            }
        }
    }

    fun updatePersona(dni: Int, name: String?, surname1: String?, surname2: String?, birthDate: Date?, phone: String?, email: String?, role: String) {
        val sql = "UPDATE PERSONA SET nom = ?, cognom1 = ?, cognom2 = ?, data_naix = ?, telefon = ?, mail = ? WHERE dni = ?"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setString(1, name)
                it.setString(2, surname1)
                it.setString(3, surname2)
                it.setDate(4, birthDate)
                it.setString(5, phone)
                it.setString(6, email)
                it.setInt(7, dni)
                it.executeUpdate()
            }
        }
    }

    fun deletePersona(dni: Int) {
        val sql = "DELETE FROM PERSONA WHERE dni = ?"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.executeUpdate()
            }
        }
    }

    fun listPersonas(offset: Int = 0, type: String = "persona"): String {
        val dniColumn = if (type == "persona") "dni" else "dni_$type"
        val sql = if (type == "persona") {
            "SELECT $dniColumn, nom, cognom1, cognom2, data_naix, telefon, mail FROM $type ORDER BY dni ASC LIMIT 10 OFFSET $offset"
        } else {
            "SELECT $type.$dniColumn, persona.nom, persona.cognom1, persona.cognom2, persona.data_naix, persona.telefon, persona.mail FROM $type JOIN persona ON $type.$dniColumn = persona.dni ORDER BY $type.$dniColumn ASC LIMIT 10 OFFSET $offset"
        }
        val builder = StringBuilder("📋 Persons:\n")
        try {
            Database.getConnection()?.use { conn ->
                conn.createStatement().use { stmt ->
                    val rs = stmt.executeQuery(sql)
                    var found = false
                    while (rs.next()) {
                        found = true
                        builder.append("${rs.getInt(dniColumn)} - ${rs.getString("nom")} ${rs.getString("cognom1")} ${rs.getString("cognom2")} - ${rs.getDate("data_naix")} - ${rs.getString("telefon")} - ${rs.getString("mail")}\n")
                    }
                    if (!found) {
                        builder.append("No persons found.\n")
                    }
                }
            }
        } catch (e: Exception) {
            return "❌ Error querying persons: ${e.message}"
        }
        return builder.toString()
    }

    private fun insertDoctor(dni: Int) {
        val sql = "INSERT INTO METGE (dni_metge, especialitat) VALUES (?, 'General')"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.executeUpdate()
            }
        }
    }

    private fun insertTechnician(dni: Int) {
        val sql = "INSERT INTO TECNIC (dni_tecnic, data_inici) VALUES (?, current_date)"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.executeUpdate()
            }
        }
    }

    private fun insertPatient(dni: Int) {
        val sql = "INSERT INTO PACIENT (dni_pacient, nss, genere) VALUES (?, '123456789012', 'H')"
        Database.getConnection()?.use { conn ->
            conn.prepareStatement(sql).use {
                it.setInt(1, dni)
                it.executeUpdate()
            }
        }
    }
}