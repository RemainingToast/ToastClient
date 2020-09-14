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
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
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
import kotlin.math.ceil

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

    private val blackList = HashMap<BlockPos, Int>()
    private val renderBlocks: MutableList<BlockPos> = ArrayList()

    private var oldSlot = -1
    private var newSlot = 0
    private var crystalSlot = 0
    private var breaks = 0
    private var bestBlock: BlockPos? = null
    private var bestDamage = 0.0
    private var pos: BlockPos? = null

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
        renderBlocks.clear()
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null) return@EventHook
        renderBlocks.clear()
        val validEntities = findValidEntity()
        val damageCache = DamageUtil.getDamageCache(); damageCache.clear()
        val offhand = mc.player!!.offHandStack.item === Items.END_CRYSTAL
        crystalSlot = if (mc.player!!.mainHandStack.item == Items.END_CRYSTAL) mc.player!!.inventory.selectedSlot else -1

        if (place && validEntities.isNotEmpty()) {
            val target: Entity = validEntities[0]
            val hand = if (mc.player!!.offHandStack.item == Items.END_CRYSTAL) Hand.OFF_HAND else if (mc.player!!.mainHandStack.item == Items.END_CRYSTAL) Hand.MAIN_HAND else return@EventHook
            if (autoswitch) {
                when {
                    !mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && !offhand -> mc.player!!.inventory.selectedSlot = crystalSlot
                    mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && antiweakness -> performAntiWeaknessStrike()
                }
            }
            findBestBlock(target)
            if (bestBlock != null && bestDamage >= mindamage) {
                placeBlock(bestBlock!!, hand)
            }
        }
        crystal = findCrystal(range.toDouble()) ?: return@EventHook
        renderBlocks.add(crystal!!.blockPos.down())
        when {
            explodeCheck(crystal!!) -> {
                renderBlocks.add(crystal!!.blockPos.down())
                oldSlot = mc.player!!.inventory.selectedSlot
                when {
                    breaks >= maxbreaks -> {
                        rotate = false
                        breaks = 0
                        return@EventHook
                    }
                    mc.player!!.inventory.mainHandStack.isFood && ignoreeating -> return@EventHook
                    isPickaxe(mc.player!!.inventory.mainHandStack.item) && ignorepickaxe -> return@EventHook
                    autoswitch && !mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && !offhand -> mc.player!!.inventory.selectedSlot = crystalSlot
                    mc.player!!.hasStatusEffect(StatusEffects.WEAKNESS) && antiweakness -> performAntiWeaknessStrike()
                }
                mc.interactionManager?.attackEntity(mc.player, crystal)
                mc.player!!.swingHand(if (!offhand) Hand.MAIN_HAND else Hand.OFF_HAND)
                ++breaks
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
            if (canReach(mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0), entity.boundingBox, range) && (sd == null || distance < sd)) {
                sd = distance.toDouble()
                foundCrystal = entity
            }
        }
        return foundCrystal
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
            mc.interactionManager?.attackEntity(mc.player, crystal)
            mc.player!!.swingHand(Hand.MAIN_HAND)
            if (autoswitch) mc.player!!.inventory.selectedSlot = crystalSlot
        }
        if (newSlot != -1) {
            mc.player!!.inventory.selectedSlot = newSlot
        }
    }

    private fun findValidEntity(): MutableList<Entity> {
        val entities: MutableList<Entity> = ArrayList()
        for (entity in mc.world!!.entities){
            when {
                mobs && entity is MobEntity && mc.player!!.distanceTo(entity) <= 10 -> entities.add(entity)
                players && entity is PlayerEntity && entity.displayName != mc.player!!.displayName && !entity.isCreative && !entity.isSpectator && entity.health >= 0 && mc.player!!.distanceTo(entity) <= 10 -> entities.add(entity)
                animals && entity is AnimalEntity && mc.player!!.distanceTo(entity) <= 10 -> entities.add(entity)
            }
        }
        return entities
    }

    private fun findBestBlock(t: Entity) {
        val target = t as LivingEntity
        bestBlock = null
        val r = ceil(range.toDouble()).toInt()
        for (x in -r until +r) {
            for (y in -r until +r) {
                for (z in - 2 until r + 2) {
                    val basePos = mc.player!!.blockPos.add(x, y, z)
                    var damage = 0.0
                    if (!canPlace(basePos) || blackList.containsKey(basePos) && blacklist) continue
                    if (bestBlock == null){
                        bestBlock = basePos
                        damage = getExplosionDamage(BlockPos(bestBlock!!.add(0.5, 1.0, 0.5)), target).toDouble()
                        bestDamage = damage
                    } else if (bestDamage < damage && damage < maxselfdamage) {
                        bestBlock = basePos
                        damage = getExplosionDamage(BlockPos(bestBlock!!.add(0.5, 1.0, 0.5)), target).toDouble()
                        bestDamage = damage
                    }
                }
            }
        }
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
        if (it.key == GLFW.GLFW_KEY_LEFT_SHIFT && sneaksurround && !mc.player!!.isFallFlying && mod != null && mc.player!!.isOnGround) mod.toggle()
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
            val str = getExplosionDamage(crystal!!.blockPos, mc.player!!).toDouble()
            val color = healthGradient(str, 0.0, mc.player!!.maxHealth.toDouble())
            for (pos in renderBlocks) {
                draw3d(translate = true) {
                    begin(GL11.GL_QUADS) {
                        color(color)
                        box(pos)
                        text(it.matrixStack, str.toString(), pos)
                    }
                }
            }
        }
    })

    private fun placeBlock(block: BlockPos, hand: Hand) {
        val bop = block.add(0.5, 0.5, 0.5)
        val vec1 = Vec3d(bop.x.toDouble(), bop.y.toDouble(), bop.z.toDouble())
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookOnly(getNeededYaw(vec1), getNeededPitch(vec1), mc.player!!.isOnGround))
        mc.interactionManager!!.interactBlock(mc.player, mc.world, hand, BlockHitResult(Vec3d(block.x.toDouble(), block.y.toDouble(), block.y.toDouble()), Direction.UP, block, false))
        mc.player!!.swingHand(hand)
        mc.player!!.networkHandler.sendPacket(PlayerMoveC2SPacket.LookOnly(mc.player!!.yaw, mc.player!!.pitch, mc.player!!.isOnGround))
        mc.player!!.yaw = mc.player!!.yaw
        mc.player!!.pitch = mc.player!!.pitch
    }

    private fun healthGradient(x: Double, minX: Double, maxX: Double, from: Color = Color.RED, to: Color = Color.GREEN): Color4f {
        val range = maxX - minX
        val p = (x - minX) / range
        return Color4f((from.red * p + to.red * (1 - p)).toFloat(), (from.green * p + to.green * (1 - p)).toFloat(), (from.blue * p + to.blue * (1 - p)).toFloat(), 0.3f)
    }
}

