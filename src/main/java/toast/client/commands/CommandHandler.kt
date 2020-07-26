package toast.client.commands

import net.minecraft.client.MinecraftClient
import org.reflections.Reflections
import toast.client.ToastClient
import toast.client.utils.MessageUtil
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
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val nextCommand = commandIter.next()
            val aliasIter = nextCommand.aliases.iterator()
            while (aliasIter.hasNext()) {
                val nextAlias = aliasIter.next()
                if (nextAlias.equals(name, ignoreCase = true)) {
                    try {
                        if (isDevCancel(nextCommand)) {
                            notfound = true
                            continue
                        }
                        notfound = false
                        nextCommand.run(args)
                    } catch (err: Exception) {
                        err.printStackTrace()
                        MessageUtil.sendMessage("Sorry but something went wrong", MessageUtil.Color.RED)
                    }
                }
            }
        }
        if (notfound) {
            MessageUtil.sendMessage("Cannot find command ${MessageUtil.CHAT_PREFIX} $name", MessageUtil.Color.RED)
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
        val reflections = Reflections("toast.client.commands")
        val commandClasses =
                reflections.getSubTypesOf(
                        Command::class.java
                )
        val classIter = commandClasses.iterator()
        while (classIter.hasNext()) {
            val command = classIter.next().getConstructor().newInstance()
            commands.add(command)
        }
    }
}