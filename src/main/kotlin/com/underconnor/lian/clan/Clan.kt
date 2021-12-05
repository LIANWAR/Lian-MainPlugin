package com.underconnor.lian.clan

import com.underconnor.lian.common.LianPlayer

data class Clan(
    var owner: LianPlayer? = null,
    var players: ArrayList<LianPlayer?> = arrayListOf(owner),
    val name: String
)
