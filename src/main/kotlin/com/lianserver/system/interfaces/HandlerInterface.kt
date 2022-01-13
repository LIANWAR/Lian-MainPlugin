package com.lianserver.system.interfaces

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.event.Listener

interface HandlerInterface: Listener {
    fun getInstance(): LianPlugin = LianPlugin.instance
}