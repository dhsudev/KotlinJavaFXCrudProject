package models

open class Persona(
    val dni: Int,
    val name: String,
    val surname1: String,
    val surname2: String,
    val birthDate: java.sql.Date,
    val phone: String,
    val email: String?,
    val role: String
) {
    override fun toString(): String {
        return "[${role}] $dni - $name, $surname1 $surname2 - $birthDate - $phone - $email"
    }
}

