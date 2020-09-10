package dev.toastmc.client.module.combat

import dev.toastmc.client.ToastClient
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
import dev.toastmc.client.util.WorldUtil.block
import dev.toastmc.client.util.WorldUtil.matches
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.Blocks.BEDROCK
import net.minecraft.block.Blocks.OBSIDIAN
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleManifest(
    label = "CrystalAura",
    description = "Hit crystals Automatically",
    category = Category.COMBAT,
    aliases = ["crystal"]
)
class CrystalAura : Module() {
    @Setting(name = "Explode") var explode = true
    @Setting(name = "Place") var place = true
    @Setting(name = "Range") var range = 4.0
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

    var crystal: EndCrystalEntity? = null

    override fun onEnable() {
        ToastClient.EVENT_BUS.subscribe(onTickEvent)
        ToastClient.EVENT_BUS.subscribe(inputEvent)
        ToastClient.EVENT_BUS.subscribe(onWorldRenderEvent)
    }

    override fun onDisable() {
        ToastClient.EVENT_BUS.unsubscribe(onTickEvent)
        ToastClient.EVENT_BUS.unsubscribe(inputEvent)
        ToastClient.EVENT_BUS.unsubscribe(onWorldRenderEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        val damageCache = DamageUtil.getDamageCache(); damageCache.clear()
//        if (crystal == null || crystal!!.removed) {
//            crystal = findCrystal(range)
//        }
//        if (crystal == null) {
//            return@EventHook
//        }
        crystal = findCrystal(range) ?: return@EventHook
        val offhand = mc.player!!.offHandStack.item === Items.END_CRYSTAL
        crystalSlot = if (InventoryUtils.getSlotsHotbar(Item.getRawId(Items.END_CRYSTAL)) != null) InventoryUtils.getSlotsHotbar(Item.getRawId(Items.END_CRYSTAL))!![0] else -1
        if (explodeCheck(crystal!!)) {
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
            crystal = findCrystal(range)
        } else { // Failed explodeCheck
            rotate = false
            if (oldSlot != -1) {
                mc.player?.inventory?.selectedSlot = oldSlot
                oldSlot = -1
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

    private fun explodeCheck(entity: Entity) : Boolean {
        val p = mc.player!!
        val damageSafe = getExplosionDamage(entity.blockPos, p) - p.health <= maxselfdamage - 1 || p.isInvulnerable || p.isCreative || p.isSpectator
        return damageSafe && explode && canReach(mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0), entity.boundingBox, range)
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
        if (canReach(mc.player!!.pos.add(0.0, mc.player!!.getEyeHeight(mc.player!!.pose).toDouble(), 0.0), crystal!!.boundingBox, range)) {
            val pos = crystal!!.blockPos.down()
            val str =  getExplosionDamage(crystal!!.blockPos, mc.player!!).toString()
            val color = healthGradient(str.toDouble(), 0.0, mc.player!!.maxHealth.toDouble())
            draw3d(translate = true) {
                begin(GL11.GL_QUADS) {
                    color(color)
                    box(pos)
                }
//                text(it.matrixStack, str, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            }
        }
    })

    fun canPlaceCrystal(pos: BlockPos): Boolean {
        return mc.world != null && pos.block.matches(BEDROCK, OBSIDIAN) && mc.world!!.getNonSpectatingEntities(Entity::class.java, Box(pos.up()).expand(0.0, 1.0, 0.0)).isNotEmpty()
    }

    private fun healthGradient(x: Double, minX: Double, maxX: Double, from: Color = Color.RED, to: Color = Color.GREEN): Color4f {
        val range = maxX - minX
        val p = (x - minX) / range
        return Color4f((from.red * p + to.red * (1 - p)).toFloat(), (from.green * p + to.green * (1 - p)).toFloat(), (from.blue * p + to.blue * (1 - p)).toFloat(), 0.5f)
    }
}