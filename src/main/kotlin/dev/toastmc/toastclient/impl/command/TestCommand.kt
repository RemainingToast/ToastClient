package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.managers.command.Command
import dev.toastmc.toastclient.api.util.InventoryUtil
import dev.toastmc.toastclient.api.util.does
import dev.toastmc.toastclient.api.util.register
import dev.toastmc.toastclient.api.util.rootLiteral
import net.minecraft.command.CommandSource
import net.minecraft.item.Items

object TestCommand : Command("test") {

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            does {
                val slot = InventoryUtil.getSlotWithItem(Items.BEDROCK) ?: return@does 0
                InventoryUtil.asyncTransferToOffHand(slot, 50)
                0
            }
        }
    }
}