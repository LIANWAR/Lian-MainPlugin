package com.underconnor.lian.common

import com.underconnor.lian.clan.Clan
import org.bukkit.entity.Player

abstract class LianPlayer: Player {
    var clan: Clan? = null
}