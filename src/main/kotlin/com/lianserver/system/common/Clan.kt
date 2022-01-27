package com.lianserver.system.common

class Clan(o: LianPlayer, l: Pair<Int, Int>?, p: MutableList<LianPlayer> = mutableListOf(o), n: String){
    var owner: LianPlayer = o
    var land: Pair<Int, Int>? = l
    var players: MutableList<LianPlayer> = p
    val name: String = n

    override fun toString(): String {
        return "${owner.player.uniqueId}\n${land}\n${name}\n${players.joinToString("\n") { it.player.uniqueId.toString() }}\n".trim()
    }
}
