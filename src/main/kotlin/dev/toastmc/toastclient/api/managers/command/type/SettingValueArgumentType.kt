package dev.toastmc.toastclient.api.managers.command.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.toastmc.toastclient.api.setting.Setting
import dev.toastmc.toastclient.api.util.lit
import java.util.concurrent.CompletableFuture

/**
 * @author 086
 * Modified for my client base
 */
class SettingValueArgumentType(
    dependantType: ArgumentType<Setting<*>>,
    dependantArgument: String,
    shift: Int
) : DependantArgumentType<String, Setting<*>>(
    dependantType,
    dependantArgument,
    shift
) {

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): String {
        val setting = findDependencyValue(reader)
        val string = reader.readUnquotedString()
        return string ?: throw INVALID_VALUE_EXCEPTION.create(
                arrayOf<Any>(
                    string,
                    setting.name
                )
            )
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val setting = findDependencyValue(context, Setting::class.java)
        return builder.suggest(setting.stringValue).buildFuture()
    }

    companion object {
        val INVALID_VALUE_EXCEPTION =
            DynamicCommandExceptionType { `object`: Any ->
                lit(
                    "Invalid value '" + (`object` as Array<Any>)[0] + "' for setting '" + `object`[1] + "'"
                )
            }

        fun value(
            dependantType: ArgumentType<Setting<*>>,
            dependantArgument: String,
            shift: Int
        ): SettingValueArgumentType {
            return SettingValueArgumentType(dependantType, dependantArgument, shift)
        }
    }
}