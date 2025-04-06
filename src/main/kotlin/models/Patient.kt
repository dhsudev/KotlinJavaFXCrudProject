package models

data class Patient(
    val nss: String,
    val genere: Char,
    val persona: Persona
) : Persona(
    persona.dni,
    persona.name,
    persona.surname1,
    persona.surname2,
    persona.birthDate,
    persona.phone,
    persona.email,
    Role.PATIENT_EN
) {
    override fun toString(): String {
        return super.toString() + " - $nss - $genere"
    }
}