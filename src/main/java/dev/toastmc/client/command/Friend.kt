package dev.toastmc.client.command

import com.mojang.brigadier.CommandDispatcher
import dev.toastmc.client.command.util.*
import dev.toastmc.client.util.Color
import dev.toastmc.client.util.FriendManager
import dev.toastmc.client.util.sendMessage
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandSource
import net.minecraft.util.Formatting

class Friend : Command(name = "friend") {
    private var friends = FriendManager.getFriends()
    override fun register(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher register rootLiteral(this.getName()){
            literal("add"){
                // TODO: Make Friend Argument, That Suggests Players Online
                argument("name", EntityArgumentType.players()){
                    does { ctx ->
                        val entitySelector: EntitySelector = "name" from ctx
//                        entitySelector.getPlayer(mc.profiler)
//                        if(mc.isIntegratedServerRunning && mc.server!!.commandSource != null && mc.server != null){
//                            val players = gpat.getNames(mc.server!!.commandSource)
//                            players.forEach {
//                                if (FriendUtil.isFriend(it.id)){
//                                    sendMessage("${it.name} is already friend!", Color.RED)
//                                } else {
//                                    FriendUtil.add(it.name, it.id)
//                                }
//                            }
//                        }
                        0
                    }
                }
            }
            literal("remove"){

            }
            literal("list"){
                does {
                    if(friends.isNotEmpty()){
                        val keys = friends.keys
                        var str = "Your friends: ${Formatting.GREEN}"
                        for (friend in keys){
                            str += "$friend "
                        }
                        sendMessage(str, Color.GRAY)
                    } else sendMessage("You have no friends! ha ha ha", Color.RED)
                    0
                }
            }
        }
    }
}