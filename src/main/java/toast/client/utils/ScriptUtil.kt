package toast.client.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult

fun runScript(script: List<String>) {
    val mc = MinecraftClient.getInstance()
    val player = mc.player?: return
    val intManager = mc.interactionManager?: return
    Thread {
        try {
            val scriptIter = script.listIterator()
            loop@ while (scriptIter.hasNext()) {
                val next = scriptIter.next()
                when (next) {
                    "JUMP" -> player.jump()

                    "USE" -> player.interact(player, player.activeHand)
                    "HIT" -> {
                        mc.crosshairTarget ?: continue@loop
                        if (mc.crosshairTarget is EntityHitResult) player.attack((mc.crosshairTarget as EntityHitResult).entity)
                        else if (mc.crosshairTarget is BlockHitResult) intManager.breakBlock((mc.crosshairTarget as BlockHitResult).blockPos)
                    }
                }
            }
        } catch (e: Throwable) {
            MessageUtil.sendMessage("Script failed to execute", MessageUtil.Color.RED)
        }
    }.start()
}