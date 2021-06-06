package dev.toastmc.toastclient.impl.module.misc

import dev.toastmc.toastclient.api.managers.FriendManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.scoreboard.Team
import net.minecraft.text.LiteralText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor

object ExtraTab : Module("ExtraTab", Category.MISC) {

    var tabSize = number("TabSize", 150.0, 20.0, 500.0, 1)
    var highlightFriends = bool("HighlightFriends", true)

    fun formatList(entry: PlayerListEntry): Text {
        val name = if (entry.displayName != null)
            entry.displayName
        else
            Team.modifyText(entry.scoreboardTeam, LiteralText(entry.profile.name))

        return if (highlightFriends.value && FriendManager.isFriend(entry.profile.id))
            name!!.shallowCopy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ToastColor.rainbow().rgb)))
        else
            name!!
    }

}