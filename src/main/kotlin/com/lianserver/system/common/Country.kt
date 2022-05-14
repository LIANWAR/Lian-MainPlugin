package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import net.kyori.adventure.text.serializer.ComponentSerializer
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

data class Country(override val owner: LianPlayer, var land: Pair<Int, Int>?, var players: MutableList<LianPlayer> = mutableListOf(owner), val name: String, var warDeclarationDenyCount: Int = 0, var lastWarDeclaratedTime: Date = Date(0L), var public: Boolean = true, var winCount: Int = 0): ClanLike{
    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("owner", owner.player.uniqueId)
        yc.set("land", if(land != null) "${land!!.first} ${land!!.second}" else "none")
        yc.set("players", players.map { it.player.uniqueId.toString() })
        yc.set("name", name)
        yc.set("wdc", warDeclarationDenyCount)
        yc.set("lwdt", lastWarDeclaratedTime.time)
        yc.set("public", public)
        yc.set("win_count", winCount)

        return yc
    }

    companion object {
        fun deserialize(c: YamlConfiguration): Country {
            fun inst() = LianPlugin.instance
            val ls = c.getStringList("players")

            return Country(
                inst().onlinePlayers[c.getString("owner")]!!,
                Pair(c.getString("land")!!.split(" ")[0].toInt(), c.getString("land")!!.split(" ")[1].toInt()),
                ls.map { inst().onlinePlayers[it]!! } as MutableList<LianPlayer>,
                c.getString("name")!!,
                c.getInt("wdc"),
                Date(c.getLong("lwdt")),
                c.getBoolean("public"),
                c.getInt("win_count")
            )
        }
    }
}
