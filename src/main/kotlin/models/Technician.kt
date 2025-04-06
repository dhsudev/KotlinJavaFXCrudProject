package models

import java.sql.*

data class Technician(
    val dataInici: Date,
    val persona: Persona
) : Persona(
    persona.dni,
    persona.name,
    persona.surname1,
    persona.surname2,
    persona.birthDate,
    persona.phone,
    persona.email,
    Role.TECHNICIAN_EN
) {
    override fun toString(): String {
        return super.toString() + " - $dataInici"
    }
}