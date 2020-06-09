package toast.client.commands

import net.minecraft.client.MinecraftClient
import toast.client.ToastClient
import toast.client.commands.cmds.*
import toast.client.commands.cmds.Set
import toast.client.utils.Logger
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class CommandHandler {
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

    fun isDevCancel(c: Command): Boolean {
        return c.isDev && !ToastClient.devs.contains(Objects.requireNonNull(MinecraftClient.getInstance().player)!!.displayName.asFormattedString())
    }

    val commands: CopyOnWriteArrayList<Command> = CopyOnWriteArrayList()

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

    fun initCommands() {
        commands.clear()
        // alphabetical order please
        commands.add(Bind())
        commands.add(ClearChat())
        commands.add(FOV())
        commands.add(GuiReset())
        commands.add(Help())
        commands.add(ListModules())
        commands.add(Macro())
        commands.add(MOTD())
        commands.add(Panic())
        commands.add(Prefix())
        commands.add(Reload())
        commands.add(Save())
        commands.add(Suffix())
        commands.add(Set())
        commands.add(Toggle())
    }

    companion object {
        var commands = CopyOnWriteArrayList<Command>()
    }
}