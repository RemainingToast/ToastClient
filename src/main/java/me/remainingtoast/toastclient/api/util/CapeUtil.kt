package me.remainingtoast.toastclient.api.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*

object CapeUtil {

    var capeTxt = "https://raw.githubusercontent.com/RemainingToast/toastclient/dev/cape-uuids.txt"
    var uuids: MutableList<UUID> = ArrayList()

    init {
        try {
            val url = URL(capeTxt)
            val br = BufferedReader(InputStreamReader(url.openStream()))
            val line: String = ""
            while (line == br.readLine()){
                uuids.add(UUID.fromString(line))
            }
        } catch (e: Exception) {}
    }

    fun hasCape(uuid: UUID): Boolean {
        return uuids.contains(uuid);
    }
}