package com.underconnor.lian.events

import com.underconnor.lian.plugin.LianPlugin
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

/***
 * @author underconnor
 */

class SampleEvent : Listener {
    private fun getInstance(): Plugin {
        return LianPlugin.instance
    }
}