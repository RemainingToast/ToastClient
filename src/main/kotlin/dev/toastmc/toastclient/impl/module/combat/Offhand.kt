package dev.toastmc.toastclient.impl.module.combat

import dev.toastmc.toastclient.api.managers.module.Module
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType

object Offhand : Module("OffHand", Category.COMBAT) {

    var totems = 0
    var crystals = 0
    var gapples = 0
    var moving = false
    var returning = false

    val defaultItems = arrayListOf("NONE", "GAPPLE", "CRYSTAL", "TOTEM")

    var totem = bool("Totems",  true)
    var gapple = bool("Gapples",  true)
    var crystal = bool("Crystals",  true)
    var totemHealth = number("TotemHealth", 13.5, 0.1, 20.0, 0.1)
    var rightClickGap = bool("RightClickGap", true)
    var crystalCheck = bool("CrystalCheck", true)
    var playerRange = number("PlayerRange", 9.0, 0.1, 15.0, 0.1)
    var crystalRange = number("CrystalRange", 4.5, 0.1, 6.0, 0.1)
    var notFromHotbar = bool("IgnoreHotbar", true)
    var defaultItem = mode("DefaultItem", "TOTEM", defaultItems)

    override fun onUpdate() {
        if (mc.player == null || mc.currentScreen is InventoryScreen) return

        if (mc.options.keyUse.isPressed && rightClickGap.value) {
            switchOffhand(Items.ENCHANTED_GOLDEN_APPLE, gapples)
            return
        }

        if (!moving && !returning) {
            if (totem.value) {
                totems = mc.player!!.inventory.main.stream()
                    .filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }
                    .mapToInt { obj: ItemStack -> obj.count }.sum()
            }
            if (gapple.value) {
                gapples = mc.player!!.inventory.main.stream()
                    .filter { itemStack: ItemStack -> itemStack.item === Items.ENCHANTED_GOLDEN_APPLE }
                    .mapToInt { obj: ItemStack -> obj.count }.sum()
            }
            if (crystal.value) {
                crystals = mc.player!!.inventory.main.stream()
                    .filter { itemStack: ItemStack -> itemStack.item === Items.END_CRYSTAL }
                    .mapToInt { obj: ItemStack -> obj.count }.sum()
            }
        }

        val doneSomething: Result = if (gapple.value && defaultItem.toggled("GAPPLE")) {
            switchOffhand(Items.ENCHANTED_GOLDEN_APPLE, gapples)
        } else if (crystal.value && defaultItem.toggled("CRYSTAL")) {
            switchOffhand(Items.END_CRYSTAL, crystals)
        } else {
            val crystalEntities = mc.world!!.entities.filterIsInstance<EndCrystalEntity>()
            if (
                (totem.value && defaultItem.toggled("TOTEM")) &&
                mc.player!!.health <= totemHealth.value &&
                (!crystalCheck.value || mc.world!!.players.any {
                    it.pos.distanceTo(mc.player!!.pos) < playerRange.value && crystalEntities.any { e ->
                        it.pos.distanceTo(e.pos) < crystalRange.value
                    }
                })
            ) {
                switchOffhand(Items.TOTEM_OF_UNDYING, totems)
            } else {
                Result.FAILURE
            }
        }
        if (doneSomething == Result.FAILURE) {
            println("Offhand somehow failed.")
        }
    }

    fun switchOffhand(itemType: Item, count: Int): Result {
        if (mc.player!!.offHandStack.item == itemType) {
            totems++
            return Result.FOUND
        } else {
            when {
                count > 0 -> {
                    val t =
                        ((if (itemType == Items.TOTEM_OF_UNDYING && notFromHotbar.value) 9 else 0)..44).firstOrNull { mc.player!!.inventory.getStack(it).item === itemType }
                            ?: -1
                    if (t == -1) return Result.FAILURE
                    mc.interactionManager!!.clickSlot(
                        0,
                        t + if (t < 9) 36 else 0,
                        0,
                        SlotActionType.PICKUP,
                        mc.player
                    )
                    moving = true
                    return Result.PICKING
                }
                moving -> {
                    mc.interactionManager!!.clickSlot(0, 45, 0, SlotActionType.PICKUP, mc.player)
                    moving = false
                    if (!mc.player!!.inventory.isEmpty) returning = true
                    return Result.MOVING
                }
            }
        }
        return Result.FAILURE
    }

    enum class Result {
        PICKING,
        MOVING,
        FAILURE,
        FOUND
    }
}