package models

object Role {
    const val NONE_EN = "None"
    const val DOCTOR_EN = "Doctor"
    const val TECHNICIAN_EN = "Technician"
    const val PATIENT_EN = "Patient"

    const val NONE_CA = "persona"
    const val DOCTOR_CA = "metge"
    const val TECHNICIAN_CA = "tecnic"
    const val PATIENT_CA = "pacient"

    fun toCatalan(role: String): String {
        return when (role) {
            NONE_EN -> NONE_CA
            DOCTOR_EN -> DOCTOR_CA
            TECHNICIAN_EN -> TECHNICIAN_CA
            PATIENT_EN -> PATIENT_CA
            else -> NONE_CA
        }
    }
}