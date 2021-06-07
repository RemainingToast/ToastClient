package dev.toastmc.toastclient.api.managers.command

import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.ToastClient
import dev.toastmc.toastclient.api.events.KeyEvent
import dev.toastmc.toastclient.impl.command.*
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatScreen
import org.lwjgl.glfw.GLFW
import org.quantumclient.energy.Subscribe
import java.util.*

object CommandManager : IToastClient {

    @JvmField
    var prefix = "."

    var commands: MutableList<Command> = ArrayList()

    fun init() {
        register(
            DrawCommand,
            FakePlayerCommand,
            FriendCommand,
            SetCommand,
            TestCommand,
            ToggleCommand
        )

        ToastClient.eventBus.register(this)

        logger.info("Finished loading ${commands.size} commands")
    }

    private fun register(vararg commandList: Command) {
        commands.clear()

        for (command in commandList) {
            commands.add(command)
        }

        commands.forEach {
            it.register(Command.dispatcher)
        }

        Collections.sort(commands, Comparator.comparing(Command::label))
    }


    @Subscribe
    fun on(event: KeyEvent) {
        val key = GLFW.glfwGetKeyName(event.key, event.scancode)
        if (key != null && key == prefix) {
            if (MinecraftClient.getInstance().currentScreen == null) {
                MinecraftClient.getInstance().openScreen(ChatScreen(""))
            }
        }
    }
}