package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.managers.command.Command
import dev.toastmc.toastclient.api.util.*
import dev.toastmc.toastclient.api.util.entity.clone
import net.minecraft.command.CommandSource
import net.minecraft.util.Formatting

object FakePlayerCommand : Command("fakeplayer") {

    private var spawned = false

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            does {
                // TODO take UUID/name argument to customise fakeplayer
                spawned = if (!spawned) {
                    mc.world!!.addEntity(42069, mc.player!!.clone())
                    true
                } else {
                    mc.world!!.removeEntity(42069)
                    false
                }
                message("$prefix Fake Player has been ${if (spawned) "${Formatting.GREEN}SPAWNED" else "${Formatting.RED}REMOVED"}")
                0
            }
        }
    }
}