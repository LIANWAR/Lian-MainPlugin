package com.underconnor.lian.common

class Land(oo: LianPlayer, o: MutableList<LianPlayer> = mutableListOf(oo), pos: Pair<Int, Int>) {
    val loc = pos
    val allows = o
    val originalOwner = oo

    override fun toString(): String {
        return """
            ${loc.first} ${loc.second}
            ${originalOwner.player.uniqueId}
            ${allows.map { it.player.uniqueId }.joinToString("\n")}
        """.trimIndent()
    }
}