package me.remainingtoast.toastclient.api.command

import me.remainingtoast.toastclient.client.command.ToggleModule
import org.apache.commons.lang3.CharSequenceUtils.subSequence

object CommandManager {

    var commands: MutableList<Command> = ArrayList()

    /**
     * Loads and initializes all of the commands
     */
    fun init() {
        commands.clear()
        commands.add(ToggleModule)
        commands.forEach {
            it.register(Command.dispatcher)
        }
        println("COMMAND MANAGER INITIALISED")
    }

    fun commandsToString(ignoreHelp: Boolean): String {
        var str = ""
        for (c in commands){
            if(ignoreHelp && c.getName() == "help") continue
            str += "${c.getName().capitalize()}, "
        }
        str.removeSuffix(subSequence(",", str.length - 1))
        return str
    }
}