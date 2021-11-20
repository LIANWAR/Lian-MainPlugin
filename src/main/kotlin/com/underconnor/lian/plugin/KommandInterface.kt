package com.underconnor.lian.plugin

import com.underconnor.lian.plugin.LianPlugin
import org.bukkit.plugin.Plugin

interface KommandInterface {
    fun getInstance(): Plugin = LianPlugin.instance

    fun kommand() {}
}