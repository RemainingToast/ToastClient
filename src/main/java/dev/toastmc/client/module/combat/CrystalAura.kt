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
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Items
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.sqrt

@ModuleManifest(
    label = "CrystalAura",
    description = "Hit crystals Automatically",
    category = Category.COMBAT,
    aliases = ["crystal"]
)
class CrystalAura : Module() {
    @Setting(name = "Explode") var explode = true
    @Setting(name = "Place") var place = true
    @Setting(name = "Blacklist") var blacklist = true
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

    private var oldSlot = -1
    private var newSlot = 0
    private var crystalSlot = 0
    private var breaks = 0
    private var bestBlock: MutableList<BlockPos> = ArrayList()
    private var bestDamage = 0.0
    private var offhand = false

    private var crystal: MutableList<EndCrystalEntity> = ArrayList()
    private var entities: MutableList<Entity> = ArrayList()
    private val blackList = HashMap<BlockPos, Int>()

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
        EVENT_BUS.subscribe(inputEvent)
        EVENT_BUS.subscribe(onWorldRenderEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
        EVENT_BUS.unsubscribe(inputEvent)
        EVENT_BUS.unsubscribe(onWorldRenderEvent)
//        renderBlocks.clear()
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        crystal.clear()
        entities.clear()
        bestBlock.clear()
        blackList.clear()
        findCrystals(range.toDouble())
        findValidEntities()
        offhand = mc.player!!.offHandStack.item === Items.END_CRYSTAL
        crystalSlot =
            if (mc.player!!.mainHandStack.item == Items.END_CRYSTAL) mc.player!!.inventory.selectedSlot else -1
        if (place && entities.isNotEmpty()) {
            println("test")
            val hand =
                if (mc.player!!.offHandStack.item == Items.END_CRYSTAL) Hand.OFF_HAND else if (mc.player!!.mainHandStack.item == Items.END_CRYSTAL) Hand.MAIN_HAND else return@EventHook
            if (autoswitch) {
                when {
                    !mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && !offhand -> mc.player!!.inventory.selectedSlot =
                        crystalSlot
                    mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && antiweakness -> performAntiWeaknessStrike()
                }
            }
            findBestBlocks(entities[0])
            if (bestBlock.isNotEmpty() && bestDamage >= mindamage) {
                println("Should place on ${entities[0].displayName} at ${bestBlock[0].toShortString()}")
                placeBlock(bestBlock[0], hand)
            }
        }
        when {
            explodeCheck() -> {
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
                    mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && antiweakness -> performAntiWeaknessStrike()
                }
                mc.interactionManager?.attackEntity(mc.player, crystal[0])
                mc.player!!.swingHand(if (!offhand) Hand.MAIN_HAND else Hand.OFF_HAND)
                ++breaks
//                crystal.remove(crystal[0])
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

    private fun findCrystals(range: Double) {
        var sd: Double? = null
        for (entity in mc.world!!.entities) {
            if (entity == null || entity.removed || entity !is EndCrystalEntity) continue
            val p = entity.blockPos.down()
            if (blackList.containsKey(p)) {
                if (blackList[p]!! > 0) blackList.replace(p, blackList[p]!! - 1) else blackList.remove(p)
            }
            val distance = mc.player!!.distanceTo(entity)
            if (canReach(mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0), entity.boundingBox, range) && (sd == null || distance < sd)) {
                sd = distance.toDouble()
                crystal.add(entity)
            }
        }
    }

    private fun performAntiWeaknessStrike(){
        newSlot = -1
        if (mc.player!!.inventory.mainHandStack.item !is ToolItem || mc.player!!.inventory.mainHandStack.item !is SwordItem) {
            for (l in 0..8) {
                if (mc.player!!.inventory.getStack(l).item is ToolItem || mc.player!!.inventory.getStack(l).item is SwordItem) {
                    newSlot = l
                    break
                }
            }
        } else {
            mc.interactionManager?.attackEntity(mc.player, crystal[0])
            mc.player!!.swingHand(Hand.MAIN_HAND)
            if (autoswitch) mc.player!!.inventory.selectedSlot = crystalSlot
        }
        if (newSlot != -1) {
            mc.player!!.inventory.selectedSlot = newSlot
        }
    }

    private fun findValidEntities() {
        for (entity in mc.world!!.entities){
            if (entity == null || entity.removed || entity !is LivingEntity || entity.isDead) {
                if(entities.isNotEmpty()) entities.removeAt(0)
                continue
            }
            when {
                mobs && EntityUtils.isHostile(entity) && mc.player!!.distanceTo(entity) <= 10 -> entities.add(entity)
                players && entity is PlayerEntity && entity.displayName != mc.player!!.displayName && !entity.isCreative && !entity.isSpectator && mc.player!!.distanceTo(entity) <= 10 -> entities.add(entity)
                animals && EntityUtils.isAnimal(entity) && mc.player!!.distanceTo(entity) <= 10 -> entities.add(entity)
            }
        }
    }

    private fun findBestBlocks(t: Entity) {
        val target = t as LivingEntity
        val r = ceil(range.toDouble()).toInt()
        for (x in -r until +r) {
            for (y in -r until +r) {
                for (z in - 2 until r + 2) {
                    val pos = mc.player!!.blockPos.add(x, y, z)
                    if (!canPlace(pos) || blackList.containsKey(pos) && blacklist) continue
                    if (bestBlock.isEmpty()){
                        bestBlock.add(pos)
                        bestDamage = getExplosionDamage(pos.add(0.5, 1.0, 0.5), target).toDouble()
                    } else if (bestDamage < getExplosionDamage(pos.add(0.5, 1.0, 0.5), target).toDouble() && getExplosionDamage(pos.add(0.5, 1.0, 0.5), target).toDouble() < maxselfdamage) {
                        bestBlock.add(pos)
                        bestDamage = getExplosionDamage(pos.add(0.5, 1.0, 0.5), target).toDouble()
                    }
                }
            }
        }
        println("BestBlock: $bestBlock, BestDamage: $bestDamage")

    }

    private fun canPlace(blockPos: BlockPos): Boolean{
        return (mc.world!!.getBlockState(blockPos).block === Blocks.BEDROCK || mc.world!!.getBlockState(blockPos).block === Blocks.OBSIDIAN) && isEmpty(
            blockPos.add(
                0.0,
                1.0,
                0.0
            )
        )
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

    private fun explodeCheck() : Boolean {
        if (crystal.isEmpty()) return false
        val entity = crystal[0]
        val p = mc.player!!
        val damageSafe = getExplosionDamage(entity.blockPos, p) - p.health <= maxselfdamage - 1 || p.isInvulnerable || p.isCreative || p.isSpectator
        return damageSafe && explode && canReach(mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0), entity.boundingBox, range.toDouble())
    }

    private fun canReach(point: Vec3d, aabb: Box, maxRange: Double): Boolean {
        return aabb.expand(maxRange).contains(point)
    }

    @EventHandler
    private val inputEvent = Listener(EventHook<KeyPressEvent> {
        if (mc.player == null) return@EventHook
        val mod = MODULE_MANAGER.getModuleByName("Surround")
        if (it.key == GLFW.GLFW_KEY_LEFT_SHIFT && sneaksurround && !mc.player!!.isFallFlying && mod != null && mc.player!!.isOnGround) mod.toggle()
    })

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        if (!render || crystal.isEmpty()) return@EventHook
        if (canReach(
                mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0),
                crystal[0].boundingBox,
                range.toDouble()
            )
        ) {
            val str = getExplosionDamage(crystal[0].blockPos, mc.player!!).toDouble()
            val color = healthGradient(str, 0.0, mc.player!!.maxHealth.toDouble())
            if (crystal.isNotEmpty()){
                for (cry in crystal) {
                    val pos = cry.blockPos.down()
                    draw3d(translate = true) {
                        begin(GL11.GL_QUADS) {
                            color(color)
                            box(pos)
                            text(it.matrixStack, str.toString(), pos)
                        }
                    }
                }
            }
        }
    })


    private fun placeBlock(block: BlockPos, hand: Hand) {
        val bop = block.add(0.5, 0.5, 0.5)
        val vec = Vec3d(bop.x.toDouble(), bop.y.toDouble(), bop.z.toDouble())
        val yaw = getYaw(vec)
        val pitch = getPitch(vec)
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookOnly(yaw, pitch, mc.player!!.isOnGround))
        mc.interactionManager!!.interactBlock(mc.player, mc.world, hand, BlockHitResult(Vec3d(block.x.toDouble(), block.y.toDouble(), block.y.toDouble()), Direction.UP, block, false))
        mc.player!!.swingHand(hand)
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookOnly(mc.player!!.yaw, mc.player!!.pitch, mc.player!!.isOnGround))
        mc.player!!.yaw = mc.player!!.yaw
        mc.player!!.pitch = mc.player!!.pitch
        println("Yaw: $yaw, Pitch: $pitch")
    }

    private fun getYaw(vec: Vec3d): Float {
        return mc.player!!.yaw + MathHelper.wrapDegrees(Math.toDegrees(atan2(vec.z - mc.player!!.z, vec.x - mc.player!!.x)).toFloat() - 90f - mc.player!!.yaw)
    }

    private fun getPitch(vec: Vec3d): Float {
        val diffX: Double = vec.x - mc.player!!.x
        val diffY: Double = vec.y - (mc.player!!.y + mc.player!!.getEyeHeight(mc.player!!.pose))
        val diffZ: Double = vec.z - mc.player!!.z
        val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)
        return mc.player!!.pitch + MathHelper.wrapDegrees((-Math.toDegrees(atan2(diffY, diffXZ))).toFloat() - mc.player!!.pitch)
    }

    private fun healthGradient(x: Double, minX: Double, maxX: Double, from: Color = Color.RED, to: Color = Color.GREEN): Color4f {
        val range = maxX - minX
        val p = (x - minX) / range
        return Color4f((from.red * p + to.red * (1 - p)).toFloat(), (from.green * p + to.green * (1 - p)).toFloat(), (from.blue * p + to.blue * (1 - p)).toFloat(), 0.45f)
    }
}

