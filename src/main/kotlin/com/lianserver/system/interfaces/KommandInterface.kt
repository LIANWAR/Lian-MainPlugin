package com.lianserver.system.interfaces

import com.lianserver.system.plugin.LianPlugin

interface KommandInterface: PrefixedTextInterface {
    fun getInstance(): LianPlugin = LianPlugin.instance

    fun kommand() {}
}