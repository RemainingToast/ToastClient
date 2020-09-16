package dev.toastmc.client.util

import com.google.gson.reflect.TypeToken
import net.minecraft.client.MinecraftClient
import java.io.File
import java.io.FileNotFoundException
import java.util.*

object Friend {
    private var friends: MutableList<String> = ArrayList()
    private var friendsFile = File(File(MinecraftClient.getInstance().runDirectory, "toastclient/"), "friends.json")

    fun add(name: String) {
        if (name.isNotEmpty()){
            friends.add(name.toLowerCase())
            save()
        }
    }

    fun remove(name: String){
        if (name.isNotEmpty()){
            friends.remove(name.toLowerCase())
            save()
        }
    }

    fun isFriend(name: String): Boolean{
        if(name.isNotEmpty()){
            return friends.contains(name.toLowerCase())
        }
        return false
    }

    fun getFriends(): MutableList<String> {
        load()
        return friends
    }

    fun save(){
        friendsFile.createNewFile()
        friendsFile.writeText(gson.toJson(friends))
    }

    fun load(){
        try {
            friends = ArrayList()
            val loadedFriends: MutableList<String> = gson.fromJson(friendsFile.readText(), object : TypeToken<List<String>>() {}.type)
            for (friend in loadedFriends) if(!isFriend(friend)) {
                friends.add(friend)
            }
        } catch (_: FileNotFoundException) {
        } finally {
            save()
        }
    }
}