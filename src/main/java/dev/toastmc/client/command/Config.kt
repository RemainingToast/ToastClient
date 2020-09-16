package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.ConfigUtil
import dev.toastmc.client.util.sendMessage
import net.minecraft.server.command.CommandSource
import net.minecraft.util.Formatting

class Config : Command("config") {
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral("config"){
            literal("save"){
                does{
                    ConfigUtil.save()
                    sendMessage("Config saved successfully", Color.GREEN)
                    0
                }
            }
            literal("reload"){
                does {
                    try {
                        ConfigUtil.load()
                        sendMessage("Config reloaded successfully", Color.GREEN)
                    } catch (e: Exception){
                        sendMessage("Config failed to reload", Color.RED)
                    }
                    0
                }
            }
            literal("path"){
                does {
                    val path = ConfigUtil.configFile.absolutePath
                    sendMessage("Config file found at ${Formatting.GREEN}$path", Color.GRAY)
                    0
                }
            }
        }
    }
}