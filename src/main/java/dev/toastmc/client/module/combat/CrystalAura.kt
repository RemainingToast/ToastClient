package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.event.KeyPressEvent
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.event.TickEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.*
import dev.toastmc.client.util.DamageUtil.getExplosionDamage
import dev.toastmc.client.util.ItemUtil.isPickaxe
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.Blocks
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Items
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.stream.Collectors

@ModuleManifest(
    label = "CrystalAura",
    description = "Hit crystals Automatically",
    category = Category.COMBAT,
    aliases = ["crystal"]
)
class CrystalAura : Module() {
    @Setting(name = "Explode") var explode = true
    @Setting(name = "Place") var place = true
    @Setting(name = "Range") var range = 4
    @Setting(name = "MinPlaceDamage") var mindamage = 10
    @Setting(name = "MaxSelfDamage") var maxselfdamage = 0
    @Setting(name = "MaxBreaks") var maxbreaks = 2
    @Setting(name = "AutoSwitch") var autoswitch = true
    @Setting(name = "AntiWeakness") var antiweakness = true
    @Setting(name = "IgnoreEating") var ignoreeating = true
    @Setting(name = "IgnorePickaxe") var ignorepickaxe = true
    @Setting(name = "SneakSurround") var sneaksurround = true
    @Setting(name = "Players") var players = true
    @Setting(name = "Mobs") var mobs = true
    @Setting(name = "Animals") var animals = true
    @Setting(name = "Rotate") var rotate = true
    @Setting(name = "Render") var render = true

    private val blackList = HashMap<BlockPos, Int>()

    private var oldSlot = -1
    private var newSlot = 0
    private var crystalSlot = 0
    private var breaks = 0
    private var bestBlock: Vec3d? = null
    private var bestDamage = 0.0
    private val playerPos: BlockPos? = null
    private var pos: Vec3d? = null

    var crystal: EndCrystalEntity? = null

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
        EVENT_BUS.subscribe(inputEvent)
        EVENT_BUS.subscribe(onWorldRenderEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
        EVENT_BUS.unsubscribe(inputEvent)
        EVENT_BUS.unsubscribe(onWorldRenderEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        val validPlayers = mc.world!!.players.stream()
            .filter { entityPlayer -> entityPlayer.displayName != mc.player!!.displayName }
            .filter { entityPlayer -> mc.player!!.distanceTo(entityPlayer) <= 10 }
            .filter { entityPlayer -> !entityPlayer.isCreative && !entityPlayer.isSpectator && entityPlayer.health >= 0 }
            .collect(Collectors.toList()).iterator()
        if(place) {
            var target: AbstractClientPlayerEntity?
            target = if (validPlayers.hasNext()) validPlayers.next() else return@EventHook
            var i: AbstractClientPlayerEntity? = null
            while (validPlayers.hasNext()) {
                if (i == null) {
                    i = validPlayers.next()
                    continue
                }
                if (mc.player!!.distanceTo(i) < mc.player!!.distanceTo(target)) target = i
                i = validPlayers.next()
            }
            var hand = Hand.MAIN_HAND
            if (mc.player!!.mainHandStack.item !== Items.END_CRYSTAL && mc.player!!.offHandStack.item === Items.END_CRYSTAL)
                hand =
                    Hand.OFF_HAND else if (mc.player!!.mainHandStack.item !== Items.END_CRYSTAL && mc.player!!.offHandStack.item !== Items.END_CRYSTAL) {
                return@EventHook
            }
            findValidBlocks(target!!)
            if (bestBlock != null && bestDamage >= mindamage) {
                placeBlock(bestBlock!!, hand)
            }
        }
        val damageCache = DamageUtil.getDamageCache(); damageCache.clear()
        crystal = findCrystal(range.toDouble()) ?: return@EventHook
        val offhand = mc.player!!.offHandStack.item === Items.END_CRYSTAL
        crystalSlot = if (mc.player!!.mainHandStack.item == Items.END_CRYSTAL) mc.player!!.inventory.selectedSlot else -1
        when {
            explodeCheck(crystal!!) -> {
                oldSlot = mc.player!!.inventory.selectedSlot
                when {
                    breaks >= maxbreaks -> {
                        rotate = false
                        breaks = 0
                        return@EventHook
                    }
                    mc.player!!.inventory.mainHandStack.isFood && ignoreeating -> return@EventHook
                    isPickaxe(mc.player!!.inventory.mainHandStack.item) && ignorepickaxe -> return@EventHook
                    autoswitch && !mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && !offhand -> mc.player!!.inventory.selectedSlot =
                        crystalSlot
                    mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && antiweakness -> {
                        newSlot = -1
                        if (mc.player!!.inventory.mainHandStack.item !is ToolItem || mc.player!!.inventory.mainHandStack.item !is SwordItem) {
                            for (l in 0..8) {
                                if (mc.player!!.inventory.getStack(l).item is ToolItem || mc.player!!.inventory.getStack(l).item is SwordItem) {
                                    newSlot = l
                                    break
                                }
                            }
                        } else {
                            mc.interactionManager?.attackEntity(mc.player, crystal)
                            mc.player!!.swingHand(Hand.MAIN_HAND)
                            if (autoswitch) mc.player!!.inventory.selectedSlot = crystalSlot
                        }
                        if (newSlot != -1) {
                            mc.player!!.inventory.selectedSlot = newSlot
                        }
                    }
                }
                mc.interactionManager?.attackEntity(mc.player, crystal)
                mc.player!!.swingHand(if (!offhand) Hand.MAIN_HAND else Hand.OFF_HAND)
                ++breaks
                crystal = findCrystal(range.toDouble())
            }
            else -> {
                rotate = false
                if (oldSlot != -1) {
                     mc.player?.inventory?.selectedSlot = oldSlot
                     oldSlot = -1
                }
            }
        }
    })

    private fun findCrystal(range: Double): EndCrystalEntity? {
        var foundCrystal: EndCrystalEntity? = null
        var sd: Double? = null
        for (entity in mc.world!!.entities) {
            if (entity == null || entity.removed || entity !is EndCrystalEntity) continue
            val p = entity.blockPos.down()
            if (blackList.containsKey(p)) {
                if (blackList[p]!! > 0) blackList.replace(p, blackList[p]!! - 1) else blackList.remove(p)
            }
            val distance = mc.player!!.distanceTo(entity)
            if (canReach(
                    mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0),
                    entity.boundingBox,
                    range
                ) && (sd == null || distance < sd)) {
                sd = distance.toDouble()
                foundCrystal = entity
            }
        }
        return foundCrystal
    }

    private fun findValidBlocks(target: AbstractClientPlayerEntity) {
        bestBlock = null
        for (i in mc.player!!.blockPos.x - range until mc.player!!.blockPos.x + range) {
            for (j in mc.player!!.blockPos.z - range until mc.player!!.blockPos.z + range) {
                for (k in mc.player!!.blockPos.y - 3 until mc.player!!.blockPos.y + 3) {
                    pos = Vec3d(i.toDouble(), k.toDouble(), j.toDouble())
                    if ((mc.world!!.getBlockState(BlockPos(pos)).block === Blocks.BEDROCK || mc.world!!.getBlockState(BlockPos(pos)
                        ).block === Blocks.OBSIDIAN) && isEmpty(BlockPos(pos!!.add(0.0, 1.0, 0.0)))) {
                        if (bestBlock == null) {
                            bestBlock = pos
                            bestDamage = getExplosionDamage(BlockPos(bestBlock!!.add(0.5, 1.0, 0.5)), target).toDouble()
                        }
                        if (bestDamage < getExplosionDamage(BlockPos(bestBlock!!.add(0.5, 1.0, 0.5)), target).toDouble() && (getExplosionDamage(BlockPos(bestBlock!!.add(0.5, 1.0, 0.5)), target).toDouble() < maxselfdamage)) {
                            bestBlock = pos
                            bestDamage = getExplosionDamage(BlockPos(bestBlock!!.add(0.5, 1.0, 0.5)), target).toDouble()
                        }
                    }
                }
            }
        }
    }


    private fun isEmpty(pos: BlockPos): Boolean {
        return mc.world!!.isAir(pos) && mc.world!!.getOtherEntities(
            null, Box(
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                pos.x + 1.0,
                pos.y + 2.0,
                pos.z + 1.0
            )
        ).isEmpty()
    }

    private fun explodeCheck(entity: Entity) : Boolean {
        val p = mc.player!!
        val damageSafe = getExplosionDamage(entity.blockPos, p) - p.health <= maxselfdamage - 1 || p.isInvulnerable || p.isCreative || p.isSpectator
        return damageSafe && explode && canReach(
            mc.player!!.pos.add(
                0.0,
                mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(),
                0.0
            ), entity.boundingBox, range.toDouble()
        )
    }

    private fun canReach(point: Vec3d, aabb: Box, maxRange: Double): Boolean {
        return aabb.expand(maxRange).contains(point)
    }

    @EventHandler
    private val inputEvent = Listener(EventHook<KeyPressEvent> {
        if (mc.player == null) return@EventHook
        val mod = MODULE_MANAGER.getModuleByName("Surround")
        if (it.key == GLFW.GLFW_KEY_LEFT_SHIFT && sneaksurround && !mc.player!!.isFallFlying && mod != null) mod.toggle()
    })

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        if (!render || crystal == null || crystal!!.removed) return@EventHook
        if (canReach(
                mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0),
                crystal!!.boundingBox,
                range.toDouble()
            )
        ) {
            val pos = crystal!!.blockPos.down()
            val str = getExplosionDamage(crystal!!.blockPos, mc.player!!).toString()
            val color = healthGradient(str.toDouble(), 0.0, mc.player!!.maxHealth.toDouble())
            draw3d(translate = true) {
                begin(GL11.GL_QUADS) {
                    color(color)
                    box(pos)
                }
            }
        }
    })

    private fun placeBlock(block: Vec3d, hand: Hand) {
        val yaw = mc.player!!.yaw
        val pitch = mc.player!!.pitch
        val vec1 = block.add(0.5, 0.5, 0.5)
        var packet = PlayerMoveC2SPacket.LookOnly(getNeededYaw(vec1), getNeededPitch(vec1), mc.player!!.isOnGround)
        mc.player!!.networkHandler.sendPacket(packet)
        mc.interactionManager!!.interactBlock(
            mc.player, mc.world, hand, BlockHitResult(
                block, Direction.UP, BlockPos(
                    block
                ), false
            )
        )
        mc.player!!.swingHand(Hand.MAIN_HAND)
        packet = PlayerMoveC2SPacket.LookOnly(yaw, pitch, mc.player!!.isOnGround)
        mc.player!!.networkHandler.sendPacket(packet)
        mc.player!!.yaw = yaw
        mc.player!!.pitch = pitch
    }

    private fun healthGradient(x: Double, minX: Double, maxX: Double, from: Color = Color.RED, to: Color = Color.GREEN): Color4f {
        val range = maxX - minX
        val p = (x - minX) / range
        return Color4f(
            (from.red * p + to.red * (1 - p)).toFloat(),
            (from.green * p + to.green * (1 - p)).toFloat(),
            (from.blue * p + to.blue * (1 - p)).toFloat(),
            0.5f
        )
    }
}

