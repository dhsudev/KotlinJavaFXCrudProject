package controllers

import services.persona.PersonaService
import java.sql.Date

class PersonaController(private val service: PersonaService) {

    fun insertPersona(dni: Int, name: String, surname1: String, surname2: String, birthDate: String, phone: String, email: String?, role: String): String {
        return try {
            val date = Date.valueOf(birthDate)
            service.insertPersona(dni, name, surname1, surname2, date, phone, email, role)
            "Person inserted successfully."
        } catch (e: IllegalArgumentException) {
            "Invalid date format. Please use YYYY-MM-DD."
        }
    }

    fun updatePersona(dni: Int, name: String?, surname1: String?, surname2: String?, birthDate: String?, phone: String?, email: String?, role: String): String {
        return try {
            val date = birthDate?.let { Date.valueOf(it) }
            service.updatePersona(dni, name, surname1, surname2, date, phone, email, role)
            "Person updated successfully."
        } catch (e: IllegalArgumentException) {
            "Invalid date format. Please use YYYY-MM-DD."
        }
    }

    fun deletePersona(dni: Int): String {
        service.deletePersona(dni)
        return "Person deleted successfully."
    }

    fun listPersonas(offset: Int = 0, type: String = "persona"): String {
        return service.listPersonas(offset, type)
    }
}