package dev.toastmc.client.commands

import dev.toastmc.client.commands.cmds.*
import dev.toastmc.client.commands.cmds.Set
import dev.toastmc.client.utils.MessageUtil
import org.reflections.Reflections
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


/**
 * Manages the loading and execution of commands
 */
class CommandManager {
    var commandsSet = HashSet<Command>()
    private val aliasMap = HashMap<String, Command>()

    fun init() {
        aliasMap.clear()
        commandsSet.clear()
        register(Bind(), ClearChat(), FOV(), GuiReset(), Help(), ListModules(), Macro(), MOTD(), Panic(), Prefix(), Reload(), Save(), Set(), Suffix(), Toggle())
    }

    fun register(vararg commands: Command) {
        for (command in commands) {
            commandsSet.add(command)
            if (command.getAlias()?.isNotEmpty()!!) {
                for (com in command.getAlias()!!) {
                    aliasMap[com.toLowerCase()] = command
                }
            }
        }
    }
    var commands: CopyOnWriteArrayList<Command> = CopyOnWriteArrayList<Command>()

    /**
     * Executes the command corresponding to name with args as the argument array
     */
    fun run(name: String, args: Array<String>) {
        var notfound = true
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val nextCommand = commandIter.next()
            val aliasIter = nextCommand.getAlias()?.iterator()
            if (aliasIter != null) {
                while (aliasIter.hasNext()) {
                    val nextAlias = aliasIter.next()
                    if (nextAlias.equals(name, ignoreCase = true)) {
                        try {
                            notfound = false
                            nextCommand.run(args)
                        } catch (err: Exception) {
                            err.printStackTrace()
                            MessageUtil.sendMessage("Sorry but something went wrong", MessageUtil.Color.RED)
                        }
                    }
                }
            }
        }
        if (notfound) {
            MessageUtil.sendMessage("Cannot find command ${MessageUtil.CHAT_PREFIX} $name", MessageUtil.Color.RED)
        }
    }

    /**
     * Gets a command from it's name
     */
    fun getCommand(cmd: String?): Command? {
        val commandIter = commands.iterator()
        while (commandIter.hasNext()) {
            val next = commandIter.next()
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
        val reflections = Reflections("toast.client.commands")
        val commandClasses = reflections.getSubTypesOf(Command::class.java)
        val classIter = commandClasses.iterator()
        while (classIter.hasNext()) {
            val command = classIter.next().getConstructor().newInstance()
            commands.add(command)
            commandsSet.add(command)
        }
    }
}