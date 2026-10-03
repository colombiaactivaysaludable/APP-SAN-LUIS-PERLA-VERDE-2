package com.example.data.model

enum class UserRole(val label: String) {
    ADMIN("Administrador"),
    DT("Director Técnico"),
    JUGADOR("Jugador (Consulta)")
}

enum class PlayerPosition(val label: String) {
    ARQUERO("Arquero"),
    DEFENSA("Defensa"),
    VOLANTE("Volante"),
    DELANTERO("Delantero")
}

enum class PlayerStatus(val label: String) {
    ACTIVO("Activo"),
    LESIONADO("Lesionado"),
    SANCIONADO("Sancionado"),
    INACTIVO("Inactivo")
}

enum class CallUpStatus(val label: String) {
    CONFIRMADO("Confirmado"),
    NO_CONFIRMADO("Pendiente"),
    RECHAZADO("No asiste")
}

enum class PaymentConcept(val label: String) {
    ARBITRAJE("Arbitraje"),
    INSCRIPCION("Inscripción Torneo"),
    CUOTA_MENSUAL("Cuota Mensual"),
    UNIFORME("Uniforme"),
    OTRO("Otro concepto")
}
