package com.lianserver.system.common

class Clan(o: LianPlayer, l: Pair<Int, Int>?, p: MutableList<LianPlayer> = mutableListOf(o), n: String, ip: Boolean = true){
    var owner: LianPlayer = o
    var land: Pair<Int, Int>? = l
    var players: MutableList<LianPlayer> = p
    val name: String = n
    var public: Boolean = ip

    override fun toString(): String {
        return "${owner.player.uniqueId}\n${land}\n${name}\n${public}\n${players.joinToString("\n") { it.player.uniqueId.toString() }}\n".trim()
    }
}
