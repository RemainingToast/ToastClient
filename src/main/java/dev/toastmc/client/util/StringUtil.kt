package dev.toastmc.client.util

object StringUtil {
    fun parsePossible(string: String) : Any {
        return try {
            val doubleStr = string.toDouble()
            val intStr = doubleStr.toInt()
            if (doubleStr.compareTo(intStr) == 0) intStr else doubleStr
        } catch (e: NumberFormatException) {
            if (string.equals("true", true)) true else if (string.equals("false", true)) false else string
        }
    }

    fun getPossibleType(string: String): Class<*>? {
        return parsePossible(string).javaClass
    }
}
