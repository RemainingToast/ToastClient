package dev.toastmc.toastclient.api.command.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.StringReader.isQuotedStringStart
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.toastmc.toastclient.api.module.Module
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.setting.SettingManager
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.command.CommandSource
import java.util.concurrent.CompletableFuture

class SettingArgumentType(
    dependantType: ArgumentType<Module>,
    dependantArgument: String,
    shift: Int
) : DependantArgumentType<Setting<*>, Module>(
    dependantType,
    dependantArgument,
    shift
) {

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): Setting<*> {
        val module = findDependencyValue(reader)
        val string = when (isQuotedStringStart(reader.peek())) {
            true -> reader.readQuotedString()
            false -> reader.readUnquotedString()
        }

        val setting = SettingManager.getSettingsForMod(module).stream().filter {
            it.name.equals(string, true)
        }.findAny()

        return if (setting.isPresent) {
            setting.get()
        } else {
            throw INVALID_SETTING_EXCEPTION.create(arrayOf(string, module.getName()))
        }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions>? {
        val module = findDependencyValue(context, Module::class.java)
        fun String.quoteIfNecessary(): String {
            if (contains(' ')) return "\"$this\""
            return this
        }
        return CommandSource.suggestMatching(
            SettingManager.getSettingsForMod(module).map { it.name.quoteIfNecessary() },
            builder
        )
    }

    override fun getExamples(): Collection<String> {
        return EXAMPLES
    }

    companion object {
        private val EXAMPLES: Collection<String> = listOf("enabled", "speed")
        val INVALID_SETTING_EXCEPTION =
            DynamicCommandExceptionType { `object`: Any ->
                lit(
                    "Unknown setting '" + (`object` as Array<*>)[0] + "' for module " + `object`[1]
                )
            }

        fun setting(
            dependentType: ModuleArgumentType<Module>,
            featureArgName: String,
            shift: Int
        ): SettingArgumentType {
            return SettingArgumentType(dependentType, featureArgName, shift)
        }

    }
}