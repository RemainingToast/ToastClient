package dev.toastmc.client.util

import com.google.gson.reflect.TypeToken
import dev.toastmc.client.ToastClient.Companion.MODULE_MANAGER
import dev.toastmc.client.module.combat.Friends
import net.minecraft.client.MinecraftClient
import java.io.File
import java.io.FileNotFoundException
import java.util.*

object FriendManager {
    private var friends: MutableMap<String, UUID> = HashMap()
    private var friendsFile = File(File(MinecraftClient.getInstance().runDirectory, "toastclient/"), "friends.json")

    fun add(name: String, uuid: UUID) {
        if (name.isNotEmpty()){
            friends[name] = uuid
            if(mc.player != null){
                sendMessage("Added $name as friend", Color.GREEN)
            }
            save()
        }
    }

    fun remove(name: String, uuid: UUID){
        if (name.isNotEmpty()){
            friends.remove(name, uuid)
            if(mc.player != null){
                sendMessage("Removed $name as friend", Color.RED)
            }
            save()
        }
    }

    fun isFriend(name: String): Boolean{
        return friends.contains(name.toLowerCase())
    }

    fun isFriend(uuid: UUID): Boolean{
        return friends.containsValue(uuid)
    }

    fun getFriends(): MutableMap<String, UUID> {
        load()
        return friends
    }

    fun save(){
        if(friends.isNotEmpty()){
            friendsFile.createNewFile()
            friendsFile.writeText(gson.toJson(friends))
        }
    }

    fun load(){
        try {
            friends = HashMap()
            val loadedFriends: MutableMap<String, UUID> = gson.fromJson(friendsFile.readText(), object : TypeToken<Map<String, UUID>>() {}.type)
            for (friend in loadedFriends) if(!isFriend(friend.key)) {
                friends[friend.key] = friend.value
            }
        } catch (_: FileNotFoundException) {
        } finally {
            save()
        }
    }

    fun friendsModuleEnabled(): Boolean {
        val mod = MODULE_MANAGER.getModuleByClass(Friends::class.java) as Friends
        return mod.enabled
    }
}