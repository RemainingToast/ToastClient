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
                arrayOf("0", "０"),
                arrayOf("1", "１"),
                arrayOf("2", "２"),
                arrayOf("3", "３"),
                arrayOf("4", "４"),
                arrayOf("5", "５"),
                arrayOf("6", "６"),
                arrayOf("7", "７"),
                arrayOf("8", "８"),
                arrayOf("9", "９"),
                arrayOf("A", "Ａ"),
                arrayOf("B", "Ｂ"),
                arrayOf("C", "Ｃ"),
                arrayOf("D", "Ｄ"),
                arrayOf("E", "Ｅ"),
                arrayOf("F", "Ｆ"),
                arrayOf("G", "Ｇ"),
                arrayOf("H", "Ｈ"),
                arrayOf("I", "Ｉ"),
                arrayOf("J", "Ｊ"),
                arrayOf("K", "Ｋ"),
                arrayOf("L", "Ｌ"),
                arrayOf("M", "Ｍ"),
                arrayOf("N", "Ｎ"),
                arrayOf("O", "Ｏ"),
                arrayOf("P", "Ｐ"),
                arrayOf("Q", "Ｑ"),
                arrayOf("R", "Ｒ"),
                arrayOf("S", "Ｓ"),
                arrayOf("T", "Ｔ"),
                arrayOf("U", "Ｕ"),
                arrayOf("V", "Ｖ"),
                arrayOf("W", "Ｗ"),
                arrayOf("X", "Ｘ"),
                arrayOf("Y", "Ｙ"),
                arrayOf("Z", "Ｚ"),
                arrayOf("a", "ａ"),
                arrayOf("b", "ｂ"),
                arrayOf("c", "ｃ"),
                arrayOf("d", "ｄ"),
                arrayOf("e", "ｅ"),
                arrayOf("f", "ｆ"),
                arrayOf("g", "ｇ"),
                arrayOf("h", "ｈ"),
                arrayOf("i", "ｉ"),
                arrayOf("j", "ｊ"),
                arrayOf("k", "ｋ"),
                arrayOf("l", "ｌ"),
                arrayOf("m", "ｍ"),
                arrayOf("n", "ｎ"),
                arrayOf("o", "ｏ"),
                arrayOf("p", "ｐ"),
                arrayOf("q", "ｑ"),
                arrayOf("r", "ｒ"),
                arrayOf("s", "ｓ"),
                arrayOf("t", "ｔ"),
                arrayOf("u", "ｕ"),
                arrayOf("v", "ｖ"),
                arrayOf("w", "ｗ"),
                arrayOf("x", "ｘ"),
                arrayOf("y", "ｙ"),
                arrayOf("z", "ｚ"),
                arrayOf("!", "！"),
                arrayOf("\"", "＂"),
                arrayOf("#", "＃"),
                arrayOf("$", "＄"),
                arrayOf("%", "％"),
                arrayOf("&", "＆"),
                arrayOf("'", "＇"),
                arrayOf("(", "（"),
                arrayOf(")", "）"),
                arrayOf("*", "＊"),
                arrayOf("+", "＋"),
                arrayOf(",", "，"),
                arrayOf("-", "－"),
                arrayOf(".", "．"),
                arrayOf("/", "／"),
                arrayOf(":", "："),
                arrayOf(";", "；"),
                arrayOf("<", "＜"),
                arrayOf("=", "＝"),
                arrayOf(">", "＞"),
                arrayOf("?", "？"),
                arrayOf("@", "＠"),
                arrayOf("[", "［"),
                arrayOf("\\", "＼"),
                arrayOf("]", "］"),
                arrayOf("^", "＾"),
                arrayOf("_", "＿"),
                arrayOf("`", "｀"),
                arrayOf("{", "｛"),
                arrayOf("|", "｜"),
                arrayOf("}", "｝"),
                arrayOf("~", "～")
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