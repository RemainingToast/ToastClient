package dev.toastmc.toastclient.impl.module.render

import com.google.gson.reflect.TypeToken
import dev.toastmc.toastclient.IToastClient
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.network.ConnectionUtil
import net.minecraft.util.Identifier
import java.lang.reflect.Type
import java.util.*

object Capes : Module("Capes", Category.RENDER), IToastClient {

    private val users = hashMapOf<UUID, String>()

    private val capes = arrayListOf(
        Identifier("toastclient", "capes/old_mojang.png"),
        Identifier("toastclient", "capes/toast.png"),
        Identifier("toastclient", "capes/minecon_2013.png"),
    )

    private val capeTypeStrings = arrayListOf(
        "NONE",
        "CONTRIBUTOR",
        "PLUS"
    )

    var capeType = mode("Type", "NONE", capeTypeStrings)

    fun getIdentifierFromMode(): Identifier {
        when(capeType.value) {
            capeTypeStrings[0] -> return capes[0]
            capeTypeStrings[1] -> return capes[1]
            capeTypeStrings[2] -> return capes[2]
            else -> {
                return capes[0]
            }
        }
    }

    fun init() {
        val type: Type = object : TypeToken<Map<UUID, String>>(){}.getType()
        if(ConnectionUtil.getJsonFromUrl("http://localhost/capes.json") != "") {
            val cachedUsers: Map<UUID, String> =
                gson.fromJson<Map<UUID, String>>(ConnectionUtil.getJsonFromUrl("http://localhost/capes.json"), type)
            for (entry in cachedUsers.entries) {
                users.put(entry.key, entry.value)
            }
            logger.info(users.entries.toString())
        } else {
            logger.error("Failed to get capes.")
        }
    }

}
