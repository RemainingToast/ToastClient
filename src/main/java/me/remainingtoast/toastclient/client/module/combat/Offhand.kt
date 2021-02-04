package me.remainingtoast.toastclient.client.module.combat

import me.remainingtoast.toastclient.ToastClient.Companion.EVENT_BUS
import me.remainingtoast.toastclient.api.event.TickEvent
import me.remainingtoast.toastclient.api.module.Category
import me.remainingtoast.toastclient.api.module.Module
import me.remainingtoast.toastclient.api.util.mc
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.entity.decoration.EndCrystalEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.slot.SlotActionType
import java.util.*

object Offhand : Module("OffHand", Category.COMBAT) {

    var totems = 0
    var crystals = 0
    var gapples = 0
    var moving = false
    var returning = false

    val defaultItems = arrayListOf("NONE", "GAPPLE", "CRYSTAL", "TOTEM")

    var totem = registerBoolean("Totems", "Use totems", true)
    var gapple = registerBoolean("Gapples", "Use gapples", true)
    var crystal = registerBoolean("Crystals", "Use crystals", true)
    var totemHealth = registerDouble("Totem Health", "Min. health until offhand to totems", 13.5, 0.1, 20.0, true)
    var rightClickGap = registerBoolean("Right Click Gap", "Right click to send gapple to offhand", true)
    var crystalCheck = registerBoolean("Crystal Check", "Check for presence of crystals before switching to totem", true)
    var playerRange = registerDouble("Player Range", "Range from yourself to check for players", 9.0, 0.1, 15.0, true)
    var crystalRange = registerDouble("Crystal Range", "Range from players to check for crystals", 4.5, 0.1, 6.0, true)
    var notFromHotbar = registerBoolean("Not From Hotbar", "Won't send crystals in hotbar to offhand", true)
    var defaultItem = registerList("Default Item", "Item to offhand by default.", defaultItems, 3)

    override fun onEnable() {
        EVENT_BUS.subscribe(onTickEvent)
    }

    override fun onDisable() {
        EVENT_BUS.unsubscribe(onTickEvent)
    }

    @EventHandler
    private val onTickEvent = Listener(EventHook<TickEvent.Client.InGame> {
        if (mc.player == null || mc.currentScreen is InventoryScreen) return@EventHook

        if (mc.options.keyUse.isPressed && rightClickGap.value) {
            switchOffhand(Items.ENCHANTED_GOLDEN_APPLE, gapples)
            return@EventHook
        }

        if (!moving && !returning) {
            val defaultItemsCopy: ArrayList<String> = ArrayList(defaultItems)
            if (totem.value) {
                if (defaultItem.valueName == "TOTEM") {
                    defaultItemsCopy.remove("TOTEM")
                    defaultItem.list = defaultItemsCopy
                }
                totems = mc.player!!.inventory.main.stream()
                    .filter { itemStack: ItemStack -> itemStack.item === Items.TOTEM_OF_UNDYING }
                    .mapToInt { obj: ItemStack -> obj.count }.sum()
            }
            if (gapple.value) {
                if (defaultItem.valueName == "GAPPLE") {
                    defaultItemsCopy.remove("GAPPLE")
                    defaultItem.list = defaultItemsCopy
                }
                gapples = mc.player!!.inventory.main.stream()
                    .filter { itemStack: ItemStack -> itemStack.item === Items.ENCHANTED_GOLDEN_APPLE }
                    .mapToInt { obj: ItemStack -> obj.count }.sum()
            }
            if (crystal.value) {
                if (defaultItem.valueName == "CRYSTAL") {
                    defaultItemsCopy.remove("CRYSTAL")
                    defaultItem.list = defaultItemsCopy
                }
                crystals = mc.player!!.inventory.main.stream()
                    .filter { itemStack: ItemStack -> itemStack.item === Items.END_CRYSTAL }
                    .mapToInt { obj: ItemStack -> obj.count }.sum()
            }
        }

        val doneSomething: Result = if (gapple.value && defaultItem.valueName == "GAPPLE") {
            switchOffhand(Items.ENCHANTED_GOLDEN_APPLE, gapples)
        } else if (crystal.value && defaultItem.valueName == "CRYSTAL") {
            switchOffhand(Items.END_CRYSTAL, crystals)
        } else {
            val crystalEntities = mc.world!!.entities.filterIsInstance<EndCrystalEntity>()
            if (
                (totem.value && defaultItem.valueName == "TOTEM") &&
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
    })

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