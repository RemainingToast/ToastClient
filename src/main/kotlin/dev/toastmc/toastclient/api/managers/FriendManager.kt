package dev.toastmc.toastclient.api.managers

import com.google.gson.reflect.TypeToken
import com.mojang.authlib.GameProfile
import dev.toastmc.toastclient.api.config.ConfigUtil
import dev.toastmc.toastclient.api.config.ConfigUtil.mainDirectory
import dev.toastmc.toastclient.api.util.entity.ToastPlayer
import net.minecraft.entity.player.PlayerEntity
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

object FriendManager {

    var friends: MutableList<ToastPlayer> = ArrayList()

    fun getFriendsNameList(): MutableList<String> {
        val friendsName: MutableList<String> = ArrayList()
        friends.forEach(Consumer { (name) -> friendsName.add(name) })
        return friendsName
    }

    fun isFriend(player: PlayerEntity): Boolean {
        for (f in friends) {
            if (f.uuid == player.uuid) return true
        }
        return false
    }

    fun isFriend(name: String): Boolean {
        for (f in friends) {
            if (f.name == name) return true
        }
        return false
    }

    fun isFriend(uuid: UUID): Boolean {
        for (f in friends) {
            if (f.uuid == uuid) return true
        }
        return false
    }

    fun getFriend(player: PlayerEntity): ToastPlayer? {
        for (f in friends) {
            if (f.uuid == player.uuid) return f
        }
        return null
    }

    fun getFriendByUUID(uuid: UUID): ToastPlayer? {
        for (f in friends) {
            if (f.uuid == uuid) return f
        }
        return null
    }

    fun getFriendByName(name: String): ToastPlayer? {
        for (f in friends) {
            if (f.name == name) return f
        }
        return null
    }

    fun addFriend(name: String, uuid: UUID) {
        friends.add(ToastPlayer(name, uuid))
    }

    fun addFriend(player: ToastPlayer) {
        friends.add(player)
    }

    fun addFriend(player: PlayerEntity) {
        friends.add(ToastPlayer(player.displayName.string, player.uuid))
    }

    fun removeFriend(name: String, uuid: UUID) {
        friends.remove(ToastPlayer(name, uuid))
    }

    fun removeFriend(player: PlayerEntity) {
        friends.remove(ToastPlayer(player.displayName.string, player.uuid))
    }

    fun removeFriend(player: ToastPlayer) {
        friends.remove(player)
    }

    fun removeFriend(player: GameProfile) {
        friends.remove(ToastPlayer(player.name, player.id))
    }

    fun saveFriends() {
        ConfigUtil.registerFile("Friends")

        val fileOutputStreamWriter = OutputStreamWriter(
            FileOutputStream("${mainDirectory}Friends.json"),
            StandardCharsets.UTF_8
        )

        fileOutputStreamWriter.write(ConfigUtil.gson.toJson(friends))
        fileOutputStreamWriter.close()
    }

    fun loadFriends() {
        try {
            val reader = FileReader("${mainDirectory}Friends.json")
            val type = object : TypeToken<MutableList<ToastPlayer>>() {}.type

            friends = ConfigUtil.gson.fromJson(reader, type)
        } catch (e: IOException) {

        }
    }
}
