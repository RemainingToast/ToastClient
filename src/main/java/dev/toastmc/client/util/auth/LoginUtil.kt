package dev.toastmc.client.util.auth

import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.mojang.util.UUIDTypeAdapter
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.client.util.Session.AccountType
import java.lang.reflect.Field
import java.net.Proxy
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException


object LoginUtil {

    private val mc = MinecraftClient.getInstance()

    private var yas: YggdrasilAuthenticationService? = null
    private var yua: YggdrasilUserAuthentication? = null
    private var ymss: YggdrasilMinecraftSessionService? = null

    init {
        yas = YggdrasilAuthenticationService(MinecraftClient.getInstance().networkProxy, "")
        yua = yas!!.createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
        ymss = yas!!.createMinecraftSessionService() as YggdrasilMinecraftSessionService
    }

    private fun setSession(newSession: Session?): Boolean {
        if (mc == null) return false
        var newField: Field? = null
        for (field in mc.javaClass.declaredFields) {
            if (field.name.equals("session", ignoreCase = true) || field.name.equals("field_1726", ignoreCase = true)) {
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
        val auth = YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
        auth.setUsername(mail); auth.setPassword(pass)
        try { auth.logIn() } catch (e: AuthenticationException) { e.printStackTrace(); return false }
        val account = Session(auth.selectedProfile.name, auth.selectedProfile.id.toString(), auth.authenticatedToken, "mojang")
        return setSession(account)
    }

    fun loginCracked(username: String): Boolean {
        if (username.isEmpty()) return false
        val session = Session(username, UUID.randomUUID().toString(), "0", "legacy")
        return setSession(session)
    }

    fun login(username: String): Session? {
        return try {
            val uuid = UUID.nameUUIDFromBytes("offline:$username".toByteArray())
            val session = Session(username, uuid.toString(), "invalidtoken", AccountType.LEGACY.name)
            setSession(session)
//            LOGGER.info("Session login (offline) successful.")
            session
        } catch (e: Exception) {
//            LOGGER.error("Session login (offline) failed: {}", e.message)
            getSession()
        }
    }

    fun login(username: String?, password: String?): CompletableFuture<Session?>? {
        return CompletableFuture.supplyAsync {
            try {
//                LOGGER.info("Logging into a new session with username")

                // Set credentials and login
                yua!!.setUsername(username)
                yua!!.setPassword(password)
                yua!!.logIn()

                // Fetch useful session data
                val name: String = yua!!.selectedProfile.name
                val uuid = UUIDTypeAdapter.fromUUID(yua!!.selectedProfile.id)
                val token: String = yua!!.authenticatedToken
                val type: String = yua!!.userType.getName()

                // Logout after fetching what is needed
                yua!!.logOut()

                // Persist the new session to the Minecraft instance
                val session = Session(name, uuid, token, type)
                setSession(session)
//                LOGGER.info("Session login successful.")
                return@supplyAsync session
            } catch (e: java.lang.Exception) {
//                LOGGER.error("Session login failed: {}", e.message)
                throw CompletionException(e)
            }
        }
    }

    private var lastStatus = Status.UNKNOWN
    private var lastStatusCheck: Long = 0

    fun getStatus(): CompletableFuture<Status?>? {
        return if (System.currentTimeMillis() - lastStatusCheck < 60000) CompletableFuture.completedFuture(lastStatus) else CompletableFuture.supplyAsync {
            val session = getSession()
            val gp = session!!.profile
            val token = session.accessToken
            val id = UUID.randomUUID().toString()
            try {
                ymss!!.joinServer(gp, token, id)
                if (ymss!!.hasJoinedServer(gp, id, null).isComplete) {
//                    LOGGER.info("Session validated.")
                    lastStatus = Status.VALID
                } else {
//                    LOGGER.info("Session invalidated.")
                    lastStatus = Status.INVALID
                }
            } catch (e: AuthenticationException) {
//                LOGGER.warn("Unable to validate the session: {}", e.message)
                lastStatus = Status.INVALID
            }
            lastStatusCheck = System.currentTimeMillis()
            lastStatus
        }
    }

    fun getSession(): Session? {
        return MinecraftClient.getInstance().session
    }
}