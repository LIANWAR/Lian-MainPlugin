package com.lianserver.system.common

import org.bukkit.OfflinePlayer

class LianPlayer(p: OfflinePlayer){
    var country: Country? = null
    val player: OfflinePlayer = p
    var clan: Clan? = null
    var prefix: String = "**null**"
    var lastCCDay: String = "19890604" // yyyymmdd
    var cash: Int = 0
    var ccStreak: Int = 0

    //region opt
    var clanChatMode: Boolean = false // false = 전체채팅
    //endregion

    override fun toString(): String {
        return "${player.uniqueId}\n${clan?.owner?.player?.uniqueId}\n${country?.owner?.player?.uniqueId}\n$prefix\n$lastCCDay\n$cash\n$ccStreak".trim()
    }
}