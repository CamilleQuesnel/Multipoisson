package com.multipoisson.app.domain

import org.junit.Assert.*
import org.junit.Test

class FishIdTest {

    // ── tableId ────────────────────────────────────────────────────────────

    @Test
    fun `tableId pads single digits with leading zero`() {
        assertEquals("table_01", FishId.tableId(1))
        assertEquals("table_09", FishId.tableId(9))
    }

    @Test
    fun `tableId formats double digits correctly`() {
        assertEquals("table_10", FishId.tableId(10))
    }

    @Test
    fun `ALL_TABLE contains exactly 10 entries`() {
        assertEquals(10, FishId.ALL_TABLE.size)
    }

    @Test
    fun `ALL_TABLE values match tableId for 1 to 10`() {
        (1..10).forEach { n ->
            assertEquals(FishId.tableId(n), FishId.ALL_TABLE[n - 1])
        }
    }

    // ── assetName ──────────────────────────────────────────────────────────

    @Test
    fun `assetName returns correct asset for table fish`() {
        assertEquals("poisson_table_01_gris_fonce", FishId.assetName(FishId.TABLE_1))
        assertEquals("poisson_table_10_jaune", FishId.assetName(FishId.TABLE_10))
    }

    @Test
    fun `assetName returns correct asset for regularity fish`() {
        assertEquals("regular-pal01", FishId.assetName(FishId.REGULAR_7))
        assertEquals("regular-pal02", FishId.assetName(FishId.REGULAR_20))
        assertEquals("regular-pal03", FishId.assetName(FishId.REGULAR_50))
    }

    @Test
    fun `assetName returns correct asset for performance fish`() {
        assertEquals("perf-pal01", FishId.assetName(FishId.PERF_50_80))
        assertEquals("perf-pal02", FishId.assetName(FishId.PERF_100_90))
        assertEquals("perf-pal03", FishId.assetName(FishId.PERF_100_95))
    }

    @Test
    fun `assetName returns legendary asset for legendary fish`() {
        assertEquals("basique-arc-en-ciel-content", FishId.assetName(FishId.LEGENDARY))
    }

    @Test
    fun `assetName returns bouquet asset for bouquet fish`() {
        assertEquals("bouquet-final", FishId.assetName(FishId.BOUQUET))
    }

    @Test
    fun `assetName returns fallback for unknown id`() {
        assertEquals("poisson_table_00_gris_clair", FishId.assetName("unknown_fish_xyz"))
    }

    // ── label ──────────────────────────────────────────────────────────────

    @Test
    fun `label returns correct French label for table fish`() {
        assertEquals("Table de 1", FishId.label(FishId.TABLE_1))
        assertEquals("Table de 10", FishId.label(FishId.TABLE_10))
    }

    @Test
    fun `label returns correct label for special fish`() {
        assertEquals("Bouquet final", FishId.label(FishId.BOUQUET))
        assertEquals("Légendaire", FishId.label(FishId.LEGENDARY))
    }

    @Test
    fun `label falls back to fishId for unknown`() {
        assertEquals("some_unknown_id", FishId.label("some_unknown_id"))
    }

    // ── REGULAR_THRESHOLDS ─────────────────────────────────────────────────

    @Test
    fun `REGULAR_THRESHOLDS has correct values`() {
        assertEquals(7,  FishId.REGULAR_THRESHOLDS[FishId.REGULAR_7])
        assertEquals(20, FishId.REGULAR_THRESHOLDS[FishId.REGULAR_20])
        assertEquals(50, FishId.REGULAR_THRESHOLDS[FishId.REGULAR_50])
    }
}
