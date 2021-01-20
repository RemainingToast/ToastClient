package me.remainingtoast.toastclient.api.friend

import net.minecraft.entity.player.PlayerEntity
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList


object FriendManager {

    var friends: MutableList<Friend> = ArrayList()

    fun getFriendsNameList(): MutableList<String> {
        val friendsName: MutableList<String> = ArrayList()
        friends.forEach(Consumer { (name) -> friendsName.add(name) })
        return friendsName
    }

    fun isFriend(player: PlayerEntity): Boolean {
        for(f in friends){
            if(f.uuid == player.uuid) return true
        }
         return false
    }

    fun isFriend(name: String): Boolean {
        for(f in friends){
            if(f.name == name) return true
        }
        return false
    }

    fun isFriend(uuid: UUID): Boolean {
        for(f in friends){
            if(f.uuid == uuid) return true
        }
        return false
    }

    fun getFriend(player: PlayerEntity): Friend? {
        for (f in friends){
            if(f.uuid == player.uuid) return f
        }
        return null
    }

    fun getFriendByUUID(uuid: UUID): Friend? {
        for(f in friends){
            if(f.uuid == uuid) return f
        }
        return null
    }

    fun getFriendByName(name: String): Friend? {
        for(f in friends){
            if(f.name == name) return f
        }
        return null
    }

    fun addFriend(name: String, uuid: UUID){
        friends.add(Friend(name, uuid))
    }

    fun addFriend(player: PlayerEntity){
        friends.add(Friend(player.displayName.string, player.uuid))
    }

    fun removeFriend(name: String, uuid: UUID){
        friends.remove(Friend(name, uuid))
    }

    fun removeFriend(player: PlayerEntity){
        friends.remove(Friend(player.displayName.string, player.uuid))
    }

}