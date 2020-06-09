package toast.client.utils

import net.minecraft.client.MinecraftClient
import java.lang.reflect.Field
import java.net.InetSocketAddress
import java.net.Proxy

object ProxyUtil {
    private fun setProxy(proxy: Proxy?): Boolean {
        val mc = MinecraftClient.getInstance()
        var newField: Field? = null
        for (field in mc.javaClass.declaredFields) {
            if (field.name.equals("netProxy", ignoreCase = true)) {
                newField = field
            }
        }
        return try {
            newField!!.isAccessible = true
            newField[mc] = proxy
            newField.isAccessible = false
            true
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            false
        }
    }

    fun connectProxy(proxyIP: String?, proxyPort: Int, type: Proxy.Type?): Boolean {
        val proxy = Proxy(type, InetSocketAddress(proxyIP, proxyPort))
        return setProxy(proxy)
    }
}