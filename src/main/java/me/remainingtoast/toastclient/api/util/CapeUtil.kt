package me.remainingtoast.toastclient.api.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*

object CapeUtil {

    var uuids: MutableList<UUID> = ArrayList()

    fun getCapes() {
        uuids.clear()
        try {
            val str = "https://raw.githubusercontent.com/RemainingToast/toastclient/dev/src/main/resources/assets/toastclient/capes/users.txt"
            val url = URL(str)
            val br = BufferedReader(InputStreamReader(url.openStream()))
            val capes = br.readLines()
//            println("All Cape Owners: $capes")
            for (c in capes){
                val id = UUID.fromString(c)
                if (id != null){
                    uuids.add(id)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun hasCape(uuid: UUID): Boolean {
        return uuids.contains(uuid);
    }
}