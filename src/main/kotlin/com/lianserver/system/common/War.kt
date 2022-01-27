package com.lianserver.system.common

import java.util.*

data class War(val started: Date, val countries: Pair<Country, Country>){
    override fun toString(): String {
        return "${started.time}\n${countries.first.owner.player.uniqueId} ${countries.second.owner.player.uniqueId}".trim()
    }
}
