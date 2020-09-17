package dev.toastmc.client.command.util

import dev.toastmc.client.command.*
import org.apache.commons.lang3.CharSequenceUtils.subSequence

class CommandManager {

    var commands: MutableList<Command> = ArrayList()

    /**
     * Loads and initializes all of the commands
     */
    fun initCommands() {
        commands.clear()
        commands.addAll(listOf(Config(),
                Help(), Hide(), Pos(),
                Prefix(), Toggle(), Fov(), Shutdown(), Friend()
        ))
        commands.forEach {
            it.register(Command.dispatcher)
        }
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
