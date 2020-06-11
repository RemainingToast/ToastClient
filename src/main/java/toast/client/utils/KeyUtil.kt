package toast.client.utils

import kotlin.collections.LinkedHashMap

object KeyUtil {
    val keys = LinkedHashMap<String, Int>()

    fun isNumeric(str: String): Boolean {
        try {
            str.toInt()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    fun getKeyCode(char: String): Int {
        if (char.length != 1) return -1;
        return keys.getValue(char.toUpperCase())
    }

    init {
        keys[" "] = 32
        keys["'"] = 39
        keys[","] = 44
        keys["-"] = 45
        keys["."] = 46
        keys["/"] = 47
        keys["0"] = 48
        keys["1"] = 49
        keys["2"] = 50
        keys["3"] = 51
        keys["4"] = 52
        keys["5"] = 53
        keys["6"] = 54
        keys["7"] = 55
        keys["8"] = 56
        keys["9"] = 57
        keys[";"] = 59
        keys["="] = 61
        keys["A"] = 65
        keys["B"] = 66
        keys["C"] = 67
        keys["D"] = 68
        keys["E"] = 69
        keys["F"] = 70
        keys["G"] = 71
        keys["H"] = 72
        keys["I"] = 73
        keys["J"] = 74
        keys["K"] = 75
        keys["L"] = 76
        keys["M"] = 77
        keys["N"] = 78
        keys["O"] = 79
        keys["P"] = 80
        keys["Q"] = 81
        keys["R"] = 82
        keys["S"] = 83
        keys["T"] = 84
        keys["U"] = 85
        keys["V"] = 86
        keys["W"] = 87
        keys["X"] = 88
        keys["Y"] = 89
        keys["Z"] = 90
        keys["`"] = 192
        keys["["] = 219
        keys["\\"] = 220
        keys["]"] = 221
    }
}