package toast.client.utils

import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import java.lang.reflect.Field
import java.net.Proxy
import java.util.*

object LoginUtil {
    private val mc = MinecraftClient.getInstance()
    private fun setSession(newSession: Session?): Boolean {
        if (mc == null) return false
        var newField: Field? = null
        for (field in mc.javaClass.declaredFields) {
            if (field.name.equals("session", ignoreCase = true) || field.name
                    .equals("field_1726", ignoreCase = true)
            ) {
                newField = field
            }
        }
        return try {
            newField!!.isAccessible = true
            newField[mc] = newSession
            newField.isAccessible = false
            true
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    fun loginWithPass(mail: String, pass: String): Boolean {
        if (mail.isEmpty() || pass.isEmpty()) return false
        val auth = YggdrasilAuthenticationService(
            Proxy.NO_PROXY,
            ""
        ).createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
        auth.setUsername(mail)
        auth.setPassword(pass)
        try {
            auth.logIn()
        } catch (e: AuthenticationException) {
            e.printStackTrace()
            return false
        }
        val account = Session(
            auth.selectedProfile.name,
            auth.selectedProfile.id.toString(),
            auth.authenticatedToken,
            "mojang"
        )
        return setSession(account)
    }

    fun loginCracked(username: String): Boolean {
        if (username.isEmpty()) return false
        val session = Session(
            username,
            UUID.randomUUID().toString(),
            "0", "legacy"
        )
        return setSession(session)
    }
}