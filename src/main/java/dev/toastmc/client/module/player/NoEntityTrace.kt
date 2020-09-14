package dev.toastmc.client.module.player

import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest
import dev.toastmc.client.util.mc
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting
import net.minecraft.item.PickaxeItem

// Rewritten from https://github.com/MineGame159/meteor-client/blob/master/src/main/java/minegame159/meteorclient/modules/player/NoMiningTrace.java
// LICENSE: https://github.com/MineGame159/meteor-client/blob/master/LICENSE
@ModuleManifest(
        label = "NoEntityTrace",
        description = "Mine Blocks Thru Entities",
        category = Category.PLAYER
)
open class NoEntityTrace : Module() {
    @Setting(name = "") var pickaxeOnly = true
    open fun canWork(): Boolean{
        return if(enabled){
            if (pickaxeOnly) mc.player!!.mainHandStack.item is PickaxeItem || mc.player!!.offHandStack.item is PickaxeItem else true
        } else {
            false
        }
    }
}