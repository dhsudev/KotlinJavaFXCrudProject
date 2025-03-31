package services.persona

import Database
import java.sql.*

fun insertPersona(dni: Int, nom: String, cognom1: String, cognom2: String, dataNaix: Date, telefon: String, mail: String?, role: String) {
    val sql = "INSERT INTO PERSONA (dni, nom, cognom1, cognom2, data_naix, telefon, mail) VALUES (?, ?, ?, ?, ?, ?, ?)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, dni)
            it.setString(2, nom)
            it.setString(3, cognom1)
            it.setString(4, cognom2)
            it.setDate(5, dataNaix)
            it.setString(6, telefon)
            it.setString(7, mail)
            it.executeUpdate()
        }
        when (role) {
            "Metge" -> insertMetge(dni)
            "Tecnic" -> insertTecnic(dni)
            "Pacient" -> insertPacient(dni)
        }
    }
}

fun updatePersona(dni: Int, nom: String?, cognom1: String?, cognom2: String?, dataNaix: Date?, telefon: String?, mail: String?, role: String) {
    val sql = "UPDATE PERSONA SET nom = ?, cognom1 = ?, cognom2 = ?, data_naix = ?, telefon = ?, mail = ? WHERE dni = ?"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setString(1, nom)
            it.setString(2, cognom1)
            it.setString(3, cognom2)
            it.setDate(4, Date.valueOf(dataNaix))
            it.setString(5, telefon)
            it.setString(6, mail)
            it.setInt(7, dni)
            it.executeUpdate()
        }
        when (role) {
            "Metge" -> insertMetge(dni)
            "Tecnic" -> insertTecnic(dni)
            "Pacient" -> insertPacient(dni)
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

fun listPersonas(offset: Int = 0): String {
    val sql = "SELECT dni, nom, cognom1, cognom2, data_naix, telefon, mail FROM PERSONA ORDER BY dni ASC LIMIT 10 OFFSET $offset"
    val builder = StringBuilder("📋 Personas:\n")
    try {
        Database.getConnection()?.use { conn ->
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(sql)
                var found = false
                while (rs.next()) {
                    found = true
                    builder.append("${rs.getInt("dni")} - ${rs.getString("nom")} ${rs.getString("cognom1")} ${rs.getString("cognom2")} - ${rs.getDate("data_naix")} - ${rs.getString("telefon")} - ${rs.getString("mail")}\n")
                }
                if (!found) {
                    builder.append("No se encontraron personas.\n")
                }
            }
        }
    } catch (e: Exception) {
        return "❌ Error al consultar personas: ${e.message}"
    }
    return builder.toString()
}

fun insertMetge(dni: Int) {
    val sql = "INSERT INTO METGE (dni_metge, especialitat) VALUES (?, 'General')"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, dni)
            it.executeUpdate()
        }
    }
}

fun insertTecnic(dni: Int) {
    val sql = "INSERT INTO TECNIC (dni_tecnic, data_inici) VALUES (?, current_date)"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, dni)
            it.executeUpdate()
        }
    }
}

fun insertPacient(dni: Int) {
    val sql = "INSERT INTO PACIENT (dni_pacient, nss, genere) VALUES (?, '123456789012', 'H')"
    Database.getConnection()?.use { conn ->
        conn.prepareStatement(sql).use {
            it.setInt(1, dni)
            it.executeUpdate()
        }
    }
}