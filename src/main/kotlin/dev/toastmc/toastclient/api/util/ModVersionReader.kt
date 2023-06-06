package dev.toastmc.toastclient.api.util

import java.io.InputStream
import java.util.*

class ModVersionReader {
    fun readModVersion(): String? {
        val versionPropertiesStream: InputStream? = javaClass.getResourceAsStream("/version.properties")
        versionPropertiesStream?.use { stream ->
            val properties = Properties()
            properties.load(stream)
            return properties.getProperty("version")
        }
        return null
    }
}