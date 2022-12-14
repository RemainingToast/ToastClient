package me.remainingtoast.toastclient.api.util

import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

object FancyChatUtil {
    fun rainbowText(text: String): String {
        val colors = arrayOf("&c", "&6", "&e", "&a", "&b", "&9", "&d")
        val result = arrayOf("")
        val index = intArrayOf(-1)
        Arrays.stream(text.split(" ").toTypedArray()).forEach { line ->
            Arrays.stream(line.split("").toTypedArray()).forEach { letter ->
                if (index[0] + 1 >= colors.size) index[0] -= colors.size
                index[0]++
                result[0] += colors[index[0]] + letter
            }
            result[0] += " "
        }
        return result[0]
    }

    fun retardChat(text: String): String {
        val result = StringBuilder()
        var upperCase = true
        for (letter in text.split("").toTypedArray()) {
            if (letter == " ") {
                result.append(" ")
            } else {
                upperCase = if (upperCase) {
                    result.append(letter.toUpperCase())
                    false
                } else {
                    result.append(letter.toLowerCase())
                    true
                }
            }
        }
        return result.toString()
    }

    fun classicFancy(text: String): String {
        val letters = classicLetters
        val finalString = StringBuilder()
        for (letter in text.split("").toTypedArray()) {
            if (letter == " ") {
                finalString.append(" ")
            } else {
                val key = getValueFromKey(letters, letter, true)
                if (key == null) {
                    finalString.append(letter)
                } else {
                    finalString.append(key)
                }
            }
        }
        return finalString.toString()
    }

    fun spaces(text: String): String {
        val finalString = StringBuilder()
        val text2 = text.replace(" ".toRegex(), "")
        for (letter in text2.split("").toTypedArray()) {
            finalString.append(letter).append(" ")
        }
        return finalString.toString()
    }

    //    public static String grammar(String text) {
    //        String newText = StringUtil.capitalize(text);
    //        if (newText.endsWith("?") || newText.endsWith(".") || newText.endsWith("!")) {
    //            return newText;
    //        } else {
    //            return newText+".";
    //        }
    //    }

    fun getValueFromKey(map: Map<String, String>, key: String, caseSensitive: Boolean): String? {
        var valueFound: String? = null
        for ((k, v) in map) {
            if (caseSensitive && k == key) {
                valueFound = v
            } else if (!caseSensitive && k.equals(key, ignoreCase = true)) {
                valueFound = v
            }
        }
        return valueFound
    }

    // TODO: find a easier way to do this
    private val classicLetters: Map<String, String>
        get() =// TODO: find a easier way to do this
            Stream.of(
                arrayOf("0", "???"),
                arrayOf("1", "???"),
                arrayOf("2", "???"),
                arrayOf("3", "???"),
                arrayOf("4", "???"),
                arrayOf("5", "???"),
                arrayOf("6", "???"),
                arrayOf("7", "???"),
                arrayOf("8", "???"),
                arrayOf("9", "???"),
                arrayOf("A", "???"),
                arrayOf("B", "???"),
                arrayOf("C", "???"),
                arrayOf("D", "???"),
                arrayOf("E", "???"),
                arrayOf("F", "???"),
                arrayOf("G", "???"),
                arrayOf("H", "???"),
                arrayOf("I", "???"),
                arrayOf("J", "???"),
                arrayOf("K", "???"),
                arrayOf("L", "???"),
                arrayOf("M", "???"),
                arrayOf("N", "???"),
                arrayOf("O", "???"),
                arrayOf("P", "???"),
                arrayOf("Q", "???"),
                arrayOf("R", "???"),
                arrayOf("S", "???"),
                arrayOf("T", "???"),
                arrayOf("U", "???"),
                arrayOf("V", "???"),
                arrayOf("W", "???"),
                arrayOf("X", "???"),
                arrayOf("Y", "???"),
                arrayOf("Z", "???"),
                arrayOf("a", "???"),
                arrayOf("b", "???"),
                arrayOf("c", "???"),
                arrayOf("d", "???"),
                arrayOf("e", "???"),
                arrayOf("f", "???"),
                arrayOf("g", "???"),
                arrayOf("h", "???"),
                arrayOf("i", "???"),
                arrayOf("j", "???"),
                arrayOf("k", "???"),
                arrayOf("l", "???"),
                arrayOf("m", "???"),
                arrayOf("n", "???"),
                arrayOf("o", "???"),
                arrayOf("p", "???"),
                arrayOf("q", "???"),
                arrayOf("r", "???"),
                arrayOf("s", "???"),
                arrayOf("t", "???"),
                arrayOf("u", "???"),
                arrayOf("v", "???"),
                arrayOf("w", "???"),
                arrayOf("x", "???"),
                arrayOf("y", "???"),
                arrayOf("z", "???"),
                arrayOf("!", "???"),
                arrayOf("\"", "???"),
                arrayOf("#", "???"),
                arrayOf("$", "???"),
                arrayOf("%", "???"),
                arrayOf("&", "???"),
                arrayOf("'", "???"),
                arrayOf("(", "???"),
                arrayOf(")", "???"),
                arrayOf("*", "???"),
                arrayOf("+", "???"),
                arrayOf(",", "???"),
                arrayOf("-", "???"),
                arrayOf(".", "???"),
                arrayOf("/", "???"),
                arrayOf(":", "???"),
                arrayOf(";", "???"),
                arrayOf("<", "???"),
                arrayOf("=", "???"),
                arrayOf(">", "???"),
                arrayOf("?", "???"),
                arrayOf("@", "???"),
                arrayOf("[", "???"),
                arrayOf("\\", "???"),
                arrayOf("]", "???"),
                arrayOf("^", "???"),
                arrayOf("_", "???"),
                arrayOf("`", "???"),
                arrayOf("{", "???"),
                arrayOf("|", "???"),
                arrayOf("}", "???"),
                arrayOf("~", "???")
            ).collect(
                Collectors.toMap(
                    { data: Array<String> ->
                        data[0]
                    },
                    { data: Array<String> ->
                        data[1]
                    })
            )
}