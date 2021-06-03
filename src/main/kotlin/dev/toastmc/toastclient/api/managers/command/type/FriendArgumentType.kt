package dev.toastmc.toastclient.api.managers.command.type

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.toastmc.toastclient.api.managers.FriendManager
import dev.toastmc.toastclient.api.util.entity.ToastPlayer
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.command.CommandSource
import java.util.concurrent.CompletableFuture

class FriendArgumentType<ToastPlayer>(private val clazz: Class<ToastPlayer>) :
    ArgumentType<ToastPlayer> {

    @Throws(CommandSyntaxException::class)
    override fun parse(reader: StringReader): ToastPlayer {
        val string = reader.readUnquotedString()
        try {
            return clazz.cast(FriendManager.friends.filter { clazz.isInstance(it) }
                .first { it.name.equals(string, ignoreCase = true) })
        } catch (e: NoSuchElementException) {
            throw INVALID_PLAYER_EXCEPTION.create(string)
        }
    }

    override fun <S> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(FriendManager.friends.filter { clazz.isInstance(it) }
            .map { it.name }, builder)
    }

    override fun getExamples(): Collection<String> {
        return listOf("RemainingToast", "Fit")
    }

    companion object {
        val INVALID_PLAYER_EXCEPTION =
            DynamicCommandExceptionType { p -> lit("Couldn't find player '$p'") }

        fun friend(): FriendArgumentType<ToastPlayer> = FriendArgumentType(ToastPlayer::class.java)
    }
}