package com.underconnor.lian.common

import com.underconnor.lian.clan.Clan
import org.bukkit.OfflinePlayer
import org.bukkit.util.Vector

class LianPlayer(p: OfflinePlayer){
    val player: OfflinePlayer = p
    var clan: Clan? = null
    var ownedLands: List<Pair<Int, Int>> = listOf()

    //region opt
    var clanChatMode: Boolean = false // false = 전체채팅
    //endregion

    override fun toString(): String {
        return """
            ${player.uniqueId}
            ${clan?.owner?.player?.uniqueId}
            $clanChatMode
            ${
                ownedLands.joinToString("\n") {
                    "$it"
                }
            }
        """.trimIndent()
    }

    fun Pair<Int, Int>.toLianString(): String {
        return """
            ${this.first} ${this.second}
        """.trimIndent()
    }
}