package com.example.demo.domain.util

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.roundToDecimals(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}