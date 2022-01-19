package com.lianserver.system.common

class Country(o: LianPlayer, l: Pair<Int, Int>?, p: MutableList<LianPlayer> = mutableListOf(o), n: String, d: Int = 0){
    var owner: LianPlayer = o
    var players: MutableList<LianPlayer> = p
    var land: Pair<Int, Int>? = l
    val name: String = n
    var warDeclarationDenyCount: Int = d

    override fun toString(): String {
        return "${owner.player.uniqueId.toString()}\n${land}\n${name}\n${warDeclarationDenyCount}\n${players.joinToString("\n") { it.player.uniqueId.toString() }}\n".trim()
    }
}
