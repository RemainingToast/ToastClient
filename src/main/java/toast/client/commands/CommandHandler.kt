package toast.client.commands

import net.minecraft.client.MinecraftClient
import org.reflections.Reflections
import toast.client.ToastClient
import toast.client.commands.cmds.*
import toast.client.commands.cmds.Set
import toast.client.modules.Module
import toast.client.modules.ModuleManager
import toast.client.utils.Logger
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Manages the loading and execution of commands
 */
class CommandHandler {
    /**
     * Array containing the instances of all the commands
     */
    var commands: CopyOnWriteArrayList<Command> = CopyOnWriteArrayList<Command>()

    /**
     * Executes the command corresponding to name with args as the argument array
     */
    fun executeCmd(name: String, args: Array<String>) {
        var notfound = true
        for (command in commands) {
            for (alias in command.aliases) {
                if (alias.equals(name, ignoreCase = true)) {
                    try {
                        if (isDevCancel(command)) {
                            notfound = true
                            continue
                        }
                        notfound = false
                        command.run(args)
                    } catch (err: Exception) {
                        err.printStackTrace()
                        Logger.message("Sorry but something went wrong", Logger.ERR, true)
                    }
                }
            }
        }
        if (notfound) {
            Logger.message("Cannot find command " + ToastClient.cmdPrefix + name, Logger.ERR, true)
        }
    }

    /**
     * Checks if the command is developer only and if it is checks that the current player is a developer
     */
    fun isDevCancel(c: Command): Boolean {
        return c.isDev && !ToastClient.devs.contains(Objects.requireNonNull(MinecraftClient.getInstance().player)!!.displayName.asFormattedString())
    }

    /**
     * Gets a command from it's name
     */
    fun getCommand(cmd: String?): Command? {
        for (command in commands) {
            for (alias in command.aliases) {
                if (alias.equals(cmd, ignoreCase = true)) {
                    return command
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
        val reflections = Reflections("toast.client.commands")
        val commandClasses =
                reflections.getSubTypesOf(
                        Command::class.java
                )
        for (commandClass in commandClasses) {
            val command = commandClass.getConstructor().newInstance()
            commands.add(command)
        }
    }
}