package dev.toastmc.client.module.player

import dev.toastmc.client.ToastClient.Companion.EVENT_BUS
import dev.toastmc.client.event.RenderEvent
import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.*
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.item.PickaxeItem
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11


@ModuleManifest(
    label = "NoEntityTrace",
    category = Category.PLAYER
)
class NoEntityTrace : Module() {
    @Setting(name = "PickaxeOnly") var pickaxe = true
    @Setting(name = "Render") var render = true
    @Setting(name = "Red") var red = 0
    @Setting(name = "Green") var green = 255
    @Setting(name = "Blue") var blue = 0
    @Setting(name = "Alpha") var alpha = 155


    override fun onEnable() {
        if(render) EVENT_BUS.subscribe(onWorldRenderEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onWorldRenderEvent)
    }

    fun work(): Boolean{
        return if(pickaxe) mc.player!!.mainHandStack.item is PickaxeItem || mc.player!!.offHandStack.item is PickaxeItem else true
    }

    @EventHandler
    val onWorldRenderEvent = Listener(EventHook<RenderEvent.World> {
        val ray: HitResult? = mc.crosshairTarget
        if (render && ray != null && mc.player != null) {
            if (ray.type == HitResult.Type.BLOCK) {
                val blockState: BlockState = mc.world!!.getBlockState(BlockPos(ray.pos))
                if (blockState.material != Material.AIR && mc.world!!.worldBorder.contains(BlockPos(ray.pos))) {
                    val interp: Vec3d = interpolateEntity(mc.player!!, mc.tickDelta)
                    val box = Box(BlockPos(ray.pos)).offset(-interp.x, -interp.y, -interp.z)
                    draw3d(translate = true) {
                        begin(GL11.GL_QUADS) {
                            color(red, green, blue, alpha)
                            box(box)
                        }
                    }
                }
            }
        } else return@EventHook
    })
}