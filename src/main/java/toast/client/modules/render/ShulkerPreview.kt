package toast.client.modules.render

import toast.client.modules.Module

/**
 * Module to store the enabled state of ShulkerPreview, displays contents of Shulker boxes as tooltips in inventory
 */
class ShulkerPreview : Module("ShulkerPreview", "Displays contents of a shulker box when hovered over in inventory", Category.RENDER, -1) {
    override fun onEnable() {
        disable()
    }
}