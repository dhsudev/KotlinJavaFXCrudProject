package models

data class Doctor (
    val especialitat: String,
    val persona: Persona
) : Persona(
    persona.dni,
    persona.name,
    persona.surname1,
    persona.surname2,
    persona.birthDate,
    persona.phone,
    persona.email,
    Role.DOCTOR_CA
){
    override fun toString(): String {
        return super.toString() + " - $especialitat"
    }
}