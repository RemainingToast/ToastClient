package dev.toastmc.toastclient.api.command

import dev.toastmc.toastclient.impl.command.DrawCommand
import dev.toastmc.toastclient.impl.command.SettingsCommand
import dev.toastmc.toastclient.impl.command.ToggleCommand
import java.util.*


object CommandManager {

    @JvmField var prefix = "."

    var commands: MutableList<Command> = ArrayList()

    /**
     * Loads and initializes all of the commands
     */
    fun init() {
        commands.clear()
        commands.add(DrawCommand)
        commands.add(ToggleCommand)
        commands.add(SettingsCommand)
        commands.forEach {
            it.register(Command.dispatcher)
        }
        Collections.sort(commands, Comparator.comparing(Command::label))
        println("COMMAND MANAGER INITIALISED")
    }

//    fun commandsToString(ignoreHelp: Boolean): String {
//        var str = ""
//        for (c in commands){
//            if(ignoreHelp && c.getName() == "help") continue
//            str += "${c.getName().capitalize()}, "
//        }
//        str.removeSuffix(subSequence(",", str.length - 1))
//        return str
//    }
}