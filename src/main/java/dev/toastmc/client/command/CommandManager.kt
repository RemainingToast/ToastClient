package dev.toastmc.client.command

import dev.toastmc.client.command.cmds.Coords
import dev.toastmc.client.command.cmds.Help
import dev.toastmc.client.command.cmds.List
import dev.toastmc.client.command.cmds.Toggle
import dev.toastmc.client.util.MessageUtil
import kotlin.collections.HashSet

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
        commands.add(Toggle())
        commands.add(List())
        commands.add(Coords())
    }
}
