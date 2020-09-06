package dev.toastmc.client.command

import dev.toastmc.client.command.cmds.*
import dev.toastmc.client.command.cmds.List
import dev.toastmc.client.util.MessageUtil

class CommandManager () {
    var commands: HashSet<Command> = HashSet<Command>()

    fun executeCmd(name: String, args: Array<String>) {
        if (getCommand(name) != null) getCommand(name)!!.run(args) else MessageUtil.sendMessage("Command not found \"$name\".", MessageUtil.Color.RED)
    }

    /**
     * Gets a command from it's name
     */
    // TODO: FIX NOT WORKING WITH LABEL
    fun getCommand(cmd: String?): Command? {
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val next = commandIter.next()
            if((next.getLabel()?:"").toString().equals(cmd, ignoreCase = true)){
                return next
            }
            for (alias in next.getAlias()!!) {
                if (alias.equals(cmd, ignoreCase = true)) {
                    return next
                }
            }
        }
        return null
    }

    /**
     * Loads and initializes all of the commands
     */
    fun initCommands() {
        commands.clear()
        commands.addAll(
            listOf(
                Coords(),
                DarkFinder(),
                Help(),
                Hide(),
                Highest(),
                List(),
                Toggle()
            )
        )
    }
}
