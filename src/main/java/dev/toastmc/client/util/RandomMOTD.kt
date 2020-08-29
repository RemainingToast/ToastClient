package dev.toastmc.client.util

import java.util.*

object RandomMOTD {
    private var MOTDList: ArrayList<String> = object : ArrayList<String>() {}
    var size = 0
    @JvmStatic
    fun addMOTDS() {
        MOTDList.add("Toast > Meteor")
        size = MOTDList.size
    }

    @JvmStatic
    fun randomMOTD(): String {
        val random = (Math.random() * size).toInt()
        return MOTDList[random]
    }
}