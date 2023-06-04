package dev.toastmc.toastclient.impl.module.misc

import dev.toastmc.toastclient.api.managers.FriendManager
import dev.toastmc.toastclient.api.managers.module.Module
import dev.toastmc.toastclient.api.util.ToastColor
import dev.toastmc.toastclient.api.util.lit
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.scoreboard.Team
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor

object ExtraTab : Module("ExtraTab", Category.MISC) {

    var tabSize = number("TabSize", 150.0, 20.0, 500.0, 0)
    var highlightFriends = bool("HighlightFriends", true)

    fun formatList(entry: PlayerListEntry): Text {
        val name = if (entry.displayName != null)
            entry.displayName
        else
            Team.decorateName(entry.scoreboardTeam, lit(entry.profile.name))

        return if (highlightFriends.value && FriendManager.isFriend(entry.profile.id))
            name!!.copy().setStyle(Style.EMPTY.withColor(TextColor.fromRgb(ToastColor.rainbow(255).rgb)))
        else
            name!!
    }

}