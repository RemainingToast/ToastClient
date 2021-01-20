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
            val url = URL("https://gist.githubusercontent.com/RemainingToast/09cfe3ee622517c1905da71a5d447f7c/raw/fbaad0153cfcf01ffe36c439fc7f23fab3c1855e/capes")
            val br = BufferedReader(InputStreamReader(url.openStream()))
            val capes = br.readLines()
            println("All Cape Owners: $capes")
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