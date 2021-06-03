package dev.toastmc.toastclient.api.managers.command.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException

/**
 * @author 086
 * Modified for my client base
 */
abstract class DependantArgumentType<T, D>(
    private val dependantType: ArgumentType<D>,
    private val dependantArgument: String,
    private val shiftWords: Int
) : ArgumentType<T> {

    @Throws(CommandSyntaxException::class)
    protected fun findDependencyValue(reader: StringReader?): D {
        val copy = StringReader(reader)
        rewind(copy, shiftWords)
        return dependantType.parse(copy)
    }

    protected fun <S> findDependencyValue(context: CommandContext<S>, clazz: Class<D>): D {
        return context.getArgument(dependantArgument, clazz)
    }

    private fun rewind(reader: StringReader, words: Int, readQuotes: Boolean = true) {
        var words = words
        reader.cursor = 0.coerceAtLeast(reader.cursor - 1)
        while (words > 0) {
            reader.cursor =
                0.coerceAtLeast(reader.cursor - 1) // Move to the end of the previous argument
            val quoted = reader.peek() == '"'
            // Move to the front of the previous argument
            while (reader.cursor > 0) {
                reader.cursor = reader.cursor - 1
                val prev = reader.peek()
                if (quoted && prev == '"' && reader.cursor > 0) { // Unescaped quote found
                    reader.cursor--
                    if (reader.peek() != '\\') break
                } else if (!quoted && prev == ' ') break
            }
            words--
        }
        reader.skip() // We just found a space; skip it.
    }
}