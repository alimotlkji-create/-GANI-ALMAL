package com.example

import com.example.data.repository.GaniRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPasswordHashingIsSecureAndDeterministic() {
        val pass = "secret123"
        val hash1 = GaniRepository.hashPassword(pass)
        val hash2 = GaniRepository.hashPassword(pass)
        val differentHash = GaniRepository.hashPassword("otherPass")

        assertEquals(hash1, hash2)
        assertNotEquals(pass, hash1)
        assertNotEquals(hash1, differentHash)
        assertEquals(64, hash1.length) // SHA-256 hex string length
    }

    @Test
    fun testLevelCalculationThresholds() {
        val (lvl0, frac0) = GaniRepository.calculateLevelInfo(0)
        assertEquals("مبتدئ", lvl0)
        assertEquals(0f, frac0, 0.01f)

        val (lvl100, frac100) = GaniRepository.calculateLevelInfo(100)
        assertEquals("نشيط", lvl100)
        assertEquals(0f, frac100, 0.01f)

        val (lvl500, _) = GaniRepository.calculateLevelInfo(500)
        assertEquals("متقدم", lvl500)

        val (lvl800, _) = GaniRepository.calculateLevelInfo(800)
        assertEquals("محترف", lvl800)

        val (lvl1800, _) = GaniRepository.calculateLevelInfo(1800)
        assertEquals("خبير", lvl1800)

        val (lvl3500, frac3500) = GaniRepository.calculateLevelInfo(3500)
        assertEquals("أسطورة", lvl3500)
        assertEquals(1.0f, frac3500, 0.01f)
    }

    @Test
    fun testNextLevelTarget() {
        assertEquals(100L, GaniRepository.getNextLevelTarget(50))
        assertEquals(300L, GaniRepository.getNextLevelTarget(150))
        assertEquals(700L, GaniRepository.getNextLevelTarget(500))
        assertEquals(1500L, GaniRepository.getNextLevelTarget(1000))
        assertEquals(3000L, GaniRepository.getNextLevelTarget(2500))
    }

    @Test
    fun testTodayStringFormat() {
        val todayStr = GaniRepository.getTodayString()
        assertTrue(todayStr.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }
}
