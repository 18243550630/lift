package com.example.lifeservicesassistant.util

object ColorConverter {
    fun hexToRgb(hex: String): String {
        val cleanHex = hex.replace("#", "")
        return if (cleanHex.length == 6) {
            val rgb = cleanHex.chunked(2).map { it.toInt(16) }
            "${rgb[0]},${rgb[1]},${rgb[2]}"
        } else "Invalid HEX"
    }

    fun isValidHex(hex: String): Boolean {
        val pattern = "^#?([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$".toRegex()
        return pattern.matches(hex)
    }

    fun rgbToHex(r: Int, g: Int, b: Int): String {
        return String.format("#%02X%02X%02X", r, g, b)
    }

    fun rgbToCmyk(r: Int, g: Int, b: Int): String {
        val rPrime = r / 255.0
        val gPrime = g / 255.0
        val bPrime = b / 255.0
        
        val k = 1 - maxOf(rPrime, gPrime, bPrime)
        if (k == 1.0) return "0,0,0,100"
        
        val c = (1 - rPrime - k) / (1 - k)
        val m = (1 - gPrime - k) / (1 - k)
        val y = (1 - bPrime - k) / (1 - k)
        
        return "${(c * 100).toInt()},${(m * 100).toInt()}," +
               "${(y * 100).toInt()},${(k * 100).toInt()}"
    }

    fun rgbToHsv(r: Int, g: Int, b: Int): String {
        val max = maxOf(r, g, b)
        val min = minOf(r, g, b)
        val delta = max - min

        val h = when {
            delta == 0 -> 0f
            max == r -> ((g - b) / delta.toFloat()) % 6
            max == g -> (b - r) / delta.toFloat() + 2
            else -> (r - g) / delta.toFloat() + 4
        } * 60
        
        val s = if (max == 0) 0f else delta.toFloat() / max
        return "${h.toInt()}%,${(s * 100).toInt()}%,${(max * 100 / 255).toInt()}%"
    }
}