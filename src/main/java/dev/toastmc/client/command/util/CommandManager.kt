package dev.toastmc.client.command.util

import dev.toastmc.client.command.*

class CommandManager {

    var commands: MutableList<Command> = ArrayList()

    /**
     * Loads and initializes all of the commands
     */
    fun initCommands() {
        commands.clear()
        commands.addAll(listOf(
                Help(), Hide(), Pos(),
                Prefix(), Toggle()
        ))
        commands.forEach {
            it.register(Command.dispatcher)
        }
    }

    fun commandsToString(): String {
        var str = ""
        for (c in commands){
            str += "${c.getName().capitalize()}, "
        }
        str.removeSuffix(",")
        return str
    }
}
