package com.multipoisson.app.domain

object FishId {

    // ── Table fish — one per multiplication table (1–10) ──────────────────
    fun tableId(tableN: Int): String = "table_${tableN.toString().padStart(2, '0')}"

    val TABLE_1  = tableId(1)
    val TABLE_2  = tableId(2)
    val TABLE_3  = tableId(3)
    val TABLE_4  = tableId(4)
    val TABLE_5  = tableId(5)
    val TABLE_6  = tableId(6)
    val TABLE_7  = tableId(7)
    val TABLE_8  = tableId(8)
    val TABLE_9  = tableId(9)
    val TABLE_10 = tableId(10)

    val ALL_TABLE = (1..10).map { tableId(it) }

    // ── Regularity — dolphins (cumulative play days) ───────────────────────
    const val REGULAR_7  = "regular_7days"
    const val REGULAR_20 = "regular_20days"
    const val REGULAR_50 = "regular_50days"

    val ALL_REGULAR = listOf(REGULAR_7, REGULAR_20, REGULAR_50)
    val REGULAR_THRESHOLDS = mapOf(REGULAR_7 to 7, REGULAR_20 to 20, REGULAR_50 to 50)

    // ── Performance — sharks (timed sessions) ─────────────────────────────
    /** Series ≥50 at ≥80% correct, timer on */
    const val PERF_50_80  = "perf_50_80"
    /** Series ≥100 at ≥90% correct, timer on */
    const val PERF_100_90 = "perf_100_90"
    /** Series = 100 at ≥95% correct, timer on */
    const val PERF_100_95 = "perf_100_95"

    val ALL_PERF = listOf(PERF_50_80, PERF_100_90, PERF_100_95)

    // ── Specials ───────────────────────────────────────────────────────────
    /** All 10 tables mastered — golden shimmer */
    const val BOUQUET = "bouquet_final"

    /** 100% on a series of 100, timer on — rainbow holographic */
    const val LEGENDARY = "legendary"

    // ── Asset mapping — PNG filename for each fish ─────────────────────────
    private val TABLE_ASSETS = listOf(
        "poisson_table_01_gris_fonce",
        "poisson_table_02_rose_pale",
        "poisson_table_03_vert_clair",
        "poisson_table_04_vert_fonce",
        "poisson_table_05_bleu_fonce",
        "poisson_table_06_violet",
        "poisson_table_07_orange",
        "poisson_table_08_rouge",
        "poisson_table_09_marron",
        "poisson_table_10_jaune",
    )

    fun assetName(fishId: String): String = when {
        fishId.startsWith("table_") -> {
            val n = fishId.removePrefix("table_").toIntOrNull() ?: 1
            TABLE_ASSETS.getOrElse(n - 1) { TABLE_ASSETS[0] }
        }
        fishId == REGULAR_7  -> "regular-pal01"
        fishId == REGULAR_20 -> "regular-pal02"
        fishId == REGULAR_50 -> "regular-pal03"
        fishId == PERF_50_80  -> "perf-pal01"
        fishId == PERF_100_90 -> "perf-pal02"
        fishId == PERF_100_95 -> "perf-pal03"
        fishId == BOUQUET    -> "bouquet-final"
        fishId == LEGENDARY  -> "basique-arc-en-ciel-content"
        else -> "poisson_table_00_gris_clair"
    }

    /** Human-readable label for the aquarium */
    fun label(fishId: String): String = when (fishId) {
        TABLE_1  -> "Table de 1"
        TABLE_2  -> "Table de 2"
        TABLE_3  -> "Table de 3"
        TABLE_4  -> "Table de 4"
        TABLE_5  -> "Table de 5"
        TABLE_6  -> "Table de 6"
        TABLE_7  -> "Table de 7"
        TABLE_8  -> "Table de 8"
        TABLE_9  -> "Table de 9"
        TABLE_10 -> "Table de 10"
        REGULAR_7  -> "7 jours de jeu"
        REGULAR_20 -> "20 jours de jeu"
        REGULAR_50 -> "50 jours de jeu"
        PERF_50_80  -> "50 réponses, 80%+"
        PERF_100_90 -> "100 réponses, 90%+"
        PERF_100_95 -> "100 réponses, 95%+"
        BOUQUET  -> "Bouquet final"
        LEGENDARY -> "Légendaire"
        else -> fishId
    }
}
