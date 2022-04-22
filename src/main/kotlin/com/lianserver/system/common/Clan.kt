package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

class Clan(o: LianPlayer, l: Pair<Int, Int>?, p: MutableList<LianPlayer> = mutableListOf(o), n: String, ip: Boolean = true){
    var owner: LianPlayer = o
    var land: Pair<Int, Int>? = l
    var players: MutableList<LianPlayer> = p
    val name: String = n
    var public: Boolean = ip

    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("owner", owner.player.uniqueId)
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
                Pair(c.getString("land")!!.split(" ")[0].toInt(), c.getString("land")!!.split(" ")[1].toInt()),
                ls.map { inst().onlinePlayers[it]!! } as MutableList<LianPlayer>,
                c.getString("name")!!,
                c.getBoolean("public")
            )
        }
    }
}
