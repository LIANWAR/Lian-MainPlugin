package com.underconnor.lian.plugin

import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

interface DebugKommandInterface: KommandInterface {
    fun getPrefix(path:String, sender: CommandSender) {
        val prefix = getInstance().config.getString(path)
        sender.sendMessage(Component.text("${ChatColor.GOLD}Debug |${ChatColor.WHITE} '${path}' 의 값은 '${prefix?.replace("&", "§")}' 입니다."))
    } // 메시지
}