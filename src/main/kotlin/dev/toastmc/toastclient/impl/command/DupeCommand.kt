package dev.toastmc.toastclient.impl.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.toastclient.api.events.TickEvent
import dev.toastmc.toastclient.api.managers.command.Command
import dev.toastmc.toastclient.api.util.*
import net.minecraft.command.CommandSource
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import org.quantumclient.energy.Subscribe


object DupeCommand : Command("dupe") {

    val timer = TimerUtil();
    var shouldPlace = false
    var delay = 0;

    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(label) {
            literal("swing") {
                integer("packets") {
                    integer("delay") {
                        does {
                            shouldPlace = true
                            delay = it.getArgument("delay", Int::class.java) as Int
                            timer.reset()
                            for (i in 0 until it.getArgument("packets", Int::class.java) as Int) {
                                if (mc.player != null || mc.world != null) {
                                    mc.player!!.swingHand(Hand.MAIN_HAND)
                                    mc.player!!.swingHand(Hand.OFF_HAND)
                                }
                            }
                            0
                        }
                    }
                }
            }
            literal("message") {
                integer("delay") {
                    does {
                        shouldPlace = true
                        delay = it.getArgument("delay", Int::class.java) as Int
                        timer.reset()
                        mc.networkHandler!!.sendPacket(ChatMessageC2SPacket(" "))
                        0
                    }
                }
            }
        }
    }

    @Subscribe
    fun on(event: TickEvent.Client.InGame) {
        if(!shouldPlace) return
        if(timer.isDelayComplete(delay.toLong())) {
            mc.interactionManager!!.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,  mc.player!!.raycast(40.0, mc.tickDelta, false) as BlockHitResult)
            timer.reset()
            shouldPlace = false
        }
    }

}