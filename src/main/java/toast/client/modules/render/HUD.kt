package toast.client.modules.render

import toast.client.modules.Module

/**
 * Heads Up Display that shows information about the client (currently enabled modules) and a preview of the contents of the player's inventory
 */
class HUD : Module("HUD", "Heads up display, gives information about the client.", Category.RENDER, -1) {
    init {
        settings.addBoolean("Rainbow", true)
        settings.addBoolean("Watermark", true)
        settings.addSlider("Watermark Size", 0.5, 0.75, 1.5)
        settings.addBoolean("Inventory", true)
        settings.addBoolean("Inventory BG", false)
        settings.addBoolean("Armor", false)
        settings.addBoolean("SortedSet", true)
        settings.addBoolean("Right line", true)
        settings.addBoolean("Left line", true)
        settings.addBoolean("Middle line", false)
        settings.addBoolean("TPS", true)
    }
}