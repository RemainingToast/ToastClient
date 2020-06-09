package toast.client.utils

import java.util.*

object RandomMOTD {
    private var MOTDList: ArrayList<String> = object : ArrayList<String>() {}
    var size = 0
    @JvmStatic
    fun addMOTDS() {
        MOTDList.add("RemainingToast on Top")
        MOTDList.add("Wnuke nuked the client")
        MOTDList.add("Meteor who?")
        MOTDList.add("Toast > Meteor")
        MOTDList.add("Backdoored, your coordinates are 21412 73 42142")
        MOTDList.add("Fleebs did math again")
        MOTDList.add("Qther is probably out of bounds")
        MOTDList.add("Dewy doing dewy stuff")
        MOTDList.add("RemainingToast actually does code")
        MOTDList.add("Axo is the anime cum god")
        size = MOTDList.size
    }

    @JvmStatic
    fun randomMOTD(): String {
        val random = (Math.random() * size).toInt()
        return MOTDList[random]
    }
}