package com.underconnor.lian.common

import com.underconnor.lian.clan.Clan
import org.bukkit.entity.Player

class LianPlayer(p: Player){
    val player: Player = p
    var clan: Clan? = null
}