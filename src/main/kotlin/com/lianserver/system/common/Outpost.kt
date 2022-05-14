package com.lianserver.system.common

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration

data class Outpost(
    val pos: Location,
    var ownedClan: ClanLike?,
    var claimPt: MutableMap<String, Int>
){
    fun serialize(): YamlConfiguration {
        val yc = YamlConfiguration()

        yc.set("pos", pos)
        yc.set("ownedClan", ownedClan?.owner?.player?.uniqueId.toString())
        yc.set("claimPt", claimPt)

        return yc
    }

    companion object {
        fun deserialize(c: YamlConfiguration): Outpost {
            fun inst() = LianPlugin.instance

            return Outpost(
                c.getLocation("pos")!!,
                inst().getClanOrCountry(c.getString("ownedClan") ?: ""),
                c.getObject("claimPt", mutableMapOf<String, Int>()::class.java)!!
            )
        }
    }
}
