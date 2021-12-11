package com.underconnor.lian.clan

import com.underconnor.lian.common.LianPlayer

class Clan(o: LianPlayer, p: ArrayList<LianPlayer> = arrayListOf(o), n: String){
    var owner: LianPlayer = o
    var players: ArrayList<LianPlayer> = p
    val name: String = n

    override fun toString(): String {
        return """
            ${owner.player.uniqueId}
            $name
            ${players.joinToString("\n") { it.player.uniqueId.toString() }}
        """.trimIndent()
    }
}
