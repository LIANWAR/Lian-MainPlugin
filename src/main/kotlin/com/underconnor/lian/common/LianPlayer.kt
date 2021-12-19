package com.underconnor.lian.common

import org.bukkit.OfflinePlayer

class LianPlayer(p: OfflinePlayer){
    val player: OfflinePlayer = p
    var clan: Clan? = null
    var ownedLand: Land? = null

    //region opt
    var clanChatMode: Boolean = false // false = 전체채팅
    //endregion

    override fun toString(): String {
        return "${player.uniqueId.toString()}\n${clan?.owner?.player?.uniqueId}\n$clanChatMode\n$ownedLand".trim()
    }
}