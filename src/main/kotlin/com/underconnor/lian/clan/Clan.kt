package com.underconnor.lian.clan

import com.underconnor.lian.common.LianPlayer

class Clan(o: LianPlayer, n: String){
    var owner: LianPlayer = o
    var players: ArrayList<LianPlayer> = arrayListOf(owner)
    val name: String = n
}
