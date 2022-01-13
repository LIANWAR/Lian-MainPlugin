package com.lianserver.system.common

class Land(oo: LianPlayer, o: MutableList<LianPlayer> = mutableListOf(oo), pos: Pair<Int, Int>) {
    val loc = pos
    val allows = o
    val originalOwner = oo

    override fun toString(): String {
        return "${loc.first} ${loc.second}\n${originalOwner.player.uniqueId}\n${allows.joinToString("\n") {it.player.uniqueId.toString()}}".trim()
    }
}