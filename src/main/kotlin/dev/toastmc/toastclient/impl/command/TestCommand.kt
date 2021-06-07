package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.managers.command.Command
import dev.toastmc.toastclient.api.util.does
import dev.toastmc.toastclient.api.util.entity.DamageUtil
import dev.toastmc.toastclient.api.util.message
import dev.toastmc.toastclient.api.util.register
import dev.toastmc.toastclient.api.util.rootLiteral
import net.minecraft.command.CommandSource
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult

object TestCommand : Command("test") {

    private var spawned = false

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            does {
                if (mc.crosshairTarget != null && mc.crosshairTarget!!.type == HitResult.Type.BLOCK) {
                    message(DamageUtil.getCrystalDamage((mc.crosshairTarget as BlockHitResult).blockPos, mc.player!!).toString())
                }
                0
            }
        }
    }
}