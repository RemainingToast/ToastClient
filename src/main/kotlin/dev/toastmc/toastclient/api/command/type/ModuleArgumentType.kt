package dev.toastmc.toastclient.api.command.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.toastmc.toastclient.api.module.Module
import dev.toastmc.toastclient.api.module.ModuleManager
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.command.CommandSource
import java.util.concurrent.CompletableFuture

/**
 * @author 086
 * Modified for my client base
 */
class ModuleArgumentType<Module>(private val clazz: Class<Module>) : ArgumentType<Module> {

    val modules = ModuleManager.modules

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): Module {
        val string = reader.readUnquotedString()
        try {
            return clazz.cast(modules.filter { clazz.isInstance(it) }.first { it.getName().equals(string, ignoreCase = true) })
        } catch (e: NoSuchElementException) {
            throw INVALID_MODULE_EXCEPTION.create(string)
        }
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(
            modules.filter { clazz.isInstance(it) }.map { it.getName() },
            builder
        )
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    companion object {
        private val EXAMPLES: Collection<String> = listOf("AutoTotem", "NoRender")
        val INVALID_MODULE_EXCEPTION = DynamicCommandExceptionType { mod -> lit("Unknown Module '$mod'") }
        fun getModule(): ModuleArgumentType<Module> = ModuleArgumentType(Module::class.java)
    }
}