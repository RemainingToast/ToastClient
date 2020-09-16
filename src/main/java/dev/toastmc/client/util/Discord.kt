package dev.toastmc.client.util

import net.arikia.dev.drpc.DiscordEventHandlers
import net.arikia.dev.drpc.DiscordRPC
import net.arikia.dev.drpc.DiscordRichPresence


object Discord {

    private var appId = "669916916290420736"

    var ready = false
    var presence = DiscordRichPresence()

    fun start(){
        println("Discord Starting")
        DiscordRPC.discordInitialize(appId, DiscordEventHandlers.Builder().setReadyEventHandler {
            ready = true
            presence.startTimestamp = System.currentTimeMillis()
            presence.details = getDetails()
            DiscordRPC.discordUpdatePresence(presence) }
            .setDisconnectedEventHandler { _: Int, _: String? -> ready = false }
            .setErroredEventHandler { _: Int, _: String? -> ready = false }
            .build(), true)
        println(presence.largeImageText + getDetails())
    }

    fun update(){
        if(ready){
            presence.details = getDetails()
            DiscordRPC.discordUpdatePresence(presence)
        }
    }

    fun end(){
        DiscordRPC.discordShutdown()
    }


    private fun getDetails(): String {
        var string = ""
        if(mc.player != null){
            val name = mc.player!!.entityName
            when {
                mc.player!!.isSprinting -> string = "$name is sprinting"
                mc.player!!.isOnFire || mc.player!!.isInLava -> string = "$name is burning"
                mc.player!!.isSleeping -> string = "$name is sleeping"
                mc.player!!.vehicle != null -> string = "$name is riding on ${mc.player!!.vehicle!!.entityName}"
                mc.player!!.isFallFlying && !mc.player!!.isOnGround -> string = "$name is flying"
            }
        }
        return string
    }
}