package com.underconnor.lian.clan

import org.bukkit.entity.Player

data class Clan(
    var owner: Player,
    val players: ArrayList<Player> = arrayListOf(owner)
)
