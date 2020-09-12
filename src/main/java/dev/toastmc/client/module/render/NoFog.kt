package dev.toastmc.client.module.render

import dev.toastmc.client.module.Category
import dev.toastmc.client.module.Module
import dev.toastmc.client.module.ModuleManifest

@ModuleManifest(
    label = "NoFog",
    description = "Makes the nether ugly.",
    category = Category.RENDER
)
class NoFog : Module() {}