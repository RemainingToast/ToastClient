package dev.toastmc.toastclient.api.util.network

import java.net.URL
import javax.net.ssl.HttpsURLConnection

object ConnectionUtil {

    fun getJsonFromUrl(url: String): String {
        var requestHeader: (HttpsURLConnection) -> String = { header ->
            header.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            header.requestMethod = "GET"
            header.inputStream.readBytes().toString(Charsets.UTF_8)
        }
        return try {
            (URL(url).openConnection() as HttpsURLConnection).run(requestHeader)
        } catch (e: Exception) {
            ""
        }
    }

}