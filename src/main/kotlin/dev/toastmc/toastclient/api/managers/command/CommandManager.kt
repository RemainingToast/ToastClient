package dev.toastmc.toastclient.api.managers.command

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.impl.command.*
import java.util.*

object CommandManager : IToastClient {

    @JvmField
    var prefix = "."

    var commands: MutableList<Command> = ArrayList()

    fun init() {
        register(
            DrawCommand,
            FakePlayerCommand,
            FriendCommand,
            SetCommand,
            TestCommand,
            ToggleCommand
        )

        logger.info("Finished loading ${commands.size} commands")
    }

    private fun register(vararg commandList: Command) {
        commands.clear()

        for (command in commandList) {
            commands.add(command)
        }

        commands.forEach {
            it.register(Command.dispatcher)
        }

        Collections.sort(commands, Comparator.comparing(Command::label))
    }

}