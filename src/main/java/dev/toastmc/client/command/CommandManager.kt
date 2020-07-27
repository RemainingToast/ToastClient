package dev.toastmc.client.command

import dev.toastmc.client.command.cmds.Help
import dev.toastmc.client.util.MessageUtil
import java.util.concurrent.CopyOnWriteArrayList

class CommandManager () {
    var commands: CopyOnWriteArrayList<Command> = CopyOnWriteArrayList<Command>()

    fun executeCmd(name: String, args: Array<String>) {
        for (command in commands) {
            if(command.name == name){
                command.run(args)
                return
            }
        }
        MessageUtil.defaultErrorMessage()
    }

    /**
     * Gets a command from it's name
     */
    fun getCommand(cmd: String?): Command? {
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val next = commandIter.next()
            for (alias in next.aliases) {
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