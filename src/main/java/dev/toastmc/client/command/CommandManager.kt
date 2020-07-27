package dev.toastmc.client.command

import dev.toastmc.client.command.cmds.Help
import dev.toastmc.client.util.MessageUtil
import java.util.concurrent.CopyOnWriteArrayList

class CommandManager () {
    var commands: CopyOnWriteArrayList<Command> = CopyOnWriteArrayList<Command>()

    fun executeCmd(name: String, args: Array<String>) {
        if (getCommand(name) != null) getCommand(name)!!.run(args) else MessageUtil.defaultErrorMessage()
    }

    /**
     * Gets a command from it's name
     */
    fun getCommand(cmd: String?): Command? {
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val next = commandIter.next()
            for (label in next.getLabel()!!) {
                if(label.toString().equals(cmd, ignoreCase = true)){
                    return next
                }
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
        commands.add(Help())
    }
}
