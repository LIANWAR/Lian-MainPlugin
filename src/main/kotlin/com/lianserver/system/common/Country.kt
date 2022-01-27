package com.lianserver.system.common

import java.util.*

data class Country(val owner: LianPlayer, var land: Pair<Int, Int>?, var players: MutableList<LianPlayer> = mutableListOf(owner), val name: String, var warDeclarationDenyCount: Int = 0, var lastWarDeclaratedTime: Date = Date(0L)){
    override fun toString(): String {
        return "${owner.player.uniqueId}\n${land}\n${name}\n${warDeclarationDenyCount}\n${lastWarDeclaratedTime.time}\n${players.joinToString("\n") { it.player.uniqueId.toString() }}".trim()
    }
}
