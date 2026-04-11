package com.example.demo.domain.util

import kotlin.math.floor

fun levelToMinXp(level: Long): Long = (level - 1) * 10L
fun xpToLevel(xp: Long): Long = floor(xp.toInt() / 10.0).toLong() + 1
fun xpToNextLevel(xp: Long): Long = levelToMinXp(xpToLevel(xp) + 1)
fun xpPastLastLevelUp(xp: Long): Long = xp - levelToMinXp(xpToLevel(xp))
fun pctThroughCurrentLevel(xp: Long): Double = xpPastLastLevelUp(xp) / ((levelToMinXp(xpToLevel(xp) + 1)) - levelToMinXp(xpToLevel(xp))).toDouble()