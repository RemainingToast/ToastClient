package toast.client.utils

object KeyUtil {
    fun isNumeric(str: String): Boolean {
        try {
            str.toInt()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }
}