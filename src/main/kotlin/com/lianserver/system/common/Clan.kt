package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

data class Clan(
    override val owner: LianPlayer,
    var land: Pair<Int, Int>?,
    var players: MutableList<LianPlayer> = mutableListOf(owner),
    val name: String,
    var public: Boolean = true
): ClanLike {
    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("owner", owner.player.uniqueId.toString())
        yc.set("land", if(land != null) "${land!!.first} ${land!!.second}" else "none")
        yc.set("players", players.map { it.player.uniqueId.toString() })
        yc.set("name", name)
        yc.set("public", public)

        return yc
    }

    companion object {
        fun deserialize(c: YamlConfiguration): Clan {
            fun inst() = LianPlugin.instance
            val ls = c.getStringList("players")

            return Clan(
                inst().onlinePlayers[c.getString("owner")]!!,
                if(c.getString("land") != "none") Pair(c.getString("land")!!.split(" ")[0].toInt(), c.getString("land")!!.split(" ")[1].toInt()) else null,
                ls.map { inst().onlinePlayers[it]!! } as MutableList<LianPlayer>,
                c.getString("name")!!,
                c.getBoolean("public")
            )
        }
    }
}
