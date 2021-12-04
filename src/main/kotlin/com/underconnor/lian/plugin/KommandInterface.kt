package com.underconnor.lian.plugin

import com.underconnor.lian.common.LianPlayer
import com.underconnor.lian.plugin.LianPlugin
import org.bukkit.plugin.Plugin

interface KommandInterface {
    fun getInstance(): LianPlugin = LianPlugin.instance

    fun kommand() {}
}