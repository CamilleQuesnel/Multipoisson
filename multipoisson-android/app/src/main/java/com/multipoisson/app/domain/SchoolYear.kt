package com.multipoisson.app.domain

enum class SchoolYear(val label: String) {
    MATERNELLE("Maternelle"),
    CP("CP"),
    CE1("CE1"),
    CE2("CE2"),
    CM1("CM1"),
    CM2("CM2"),
    SIXIEME("6ème"),
    CINQUIEME("5ème"),
    QUATRIEME("4ème"),
    TROISIEME("3ème"),
    LYCEE_PLUS("Lycée +");

    companion object {
        fun fromLabel(label: String): SchoolYear =
            entries.firstOrNull { it.label == label } ?: CP
    }
}
