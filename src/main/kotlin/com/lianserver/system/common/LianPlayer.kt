package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

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

    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("country", country?.owner?.player?.uniqueId)
        yc.set("clan", clan?.owner?.player?.uniqueId)
        yc.set("player", player.uniqueId)
        yc.set("prefix", prefix)
        yc.set("lcd", lastCCDay)
        yc.set("cash", cash)
        yc.set("ccs", ccStreak)

        return yc
    }

    companion object {
        fun deserialize(c: YamlConfiguration): LianPlayer {
            fun inst() = LianPlugin.instance

            return LianPlayer(inst().server.getOfflinePlayer(UUID.fromString(c.getString("player"))))
        }
    }
}