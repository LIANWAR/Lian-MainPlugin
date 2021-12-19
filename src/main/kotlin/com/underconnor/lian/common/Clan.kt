package com.underconnor.lian.common

class Clan(o: LianPlayer, p: MutableList<LianPlayer> = mutableListOf(o), n: String){
    var owner: LianPlayer = o
    var players: MutableList<LianPlayer> = p
    val name: String = n

    override fun toString(): String {
        return "${owner.player.uniqueId.toString()}\n${name}\n${players.joinToString("\n") { it.player.uniqueId.toString() }}\n".trim()
    }
}
