package com.underconnor.lian.common

import com.underconnor.lian.clan.Clan
import org.bukkit.OfflinePlayer

class LianPlayer(p: OfflinePlayer){
    val player: OfflinePlayer = p
    var clan: Clan? = null

    //region opt
    var clanChatMode: Boolean = false // false = 전체채팅
    //endregion

    override fun toString(): String {
        return """
            ${player.uniqueId}
            ${clan?.owner?.player?.uniqueId}
            $clanChatMode
        """.trimIndent()
    }
}