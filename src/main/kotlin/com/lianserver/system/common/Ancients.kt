package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration

data class Ancients(
    val pos: Location,
    var ownedClan: ClanLike?,
    var claimPt: MutableMap<String, Int>
){
    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("pos", pos)
        yc.set("ownedClan", ownedClan?.owner?.player?.uniqueId.toString())
        claimPt.forEach {
            yc.set("claimPt.${it.key}", it.value)
        }

        return yc
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun deserialize(c: YamlConfiguration): Ancients {
            fun inst() = LianPlugin.instance

            return Ancients(
                c.getLocation("pos")!!,
                inst().getClanOrCountry(c.getString("ownedClan") ?: ""),
                if(c.getConfigurationSection("claimPt") != null ) c.getConfigurationSection("claimPt")!!.getValues(false) as MutableMap<String, Int> else mutableMapOf<String, Int>()
            )
        }
    }
}
