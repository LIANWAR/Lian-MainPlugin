package com.underconnor.lian.plugin

import org.bukkit.event.Listener

interface HandlerInterface: Listener {
    fun getInstance(): LianPlugin = LianPlugin.instance
}