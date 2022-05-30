package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*

data class War(val started: Date, val countries: Pair<Country, Country>){
    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("started", started)
        yc.set("countries", countries.toList().map { it.owner.player.uniqueId.toString() })

        return yc
    }

    companion object {
        fun deserialize(c: YamlConfiguration): War {
            val ls = c.getStringList("countries")

            return War(
                Date(c.getLong("started")),
                Pair(LianPlugin.instance.countries[ls[0]]!!, LianPlugin.instance.countries[ls[1]]!!)
            )
        }
    }
}
