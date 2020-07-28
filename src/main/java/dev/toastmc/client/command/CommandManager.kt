package dev.toastmc.client.command

import dev.toastmc.client.ToastClient
import dev.toastmc.client.ToastClient.Companion.CMD_PREFIX
import dev.toastmc.client.ToastClient.Companion.COMMAND_MANAGER
import dev.toastmc.client.command.cmds.Help
import dev.toastmc.client.command.cmds.Toggle
import dev.toastmc.client.event.events.PacketEvent
import dev.toastmc.client.util.MessageUtil
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import java.util.*
import kotlin.collections.HashSet

class CommandManager () {
    var commands: HashSet<Command> = HashSet<Command>()

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
        commands.add(Toggle())
    }
}
