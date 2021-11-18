package com.underconnor.lian.kommands

import com.underconnor.lian.plugin.LianPlugin
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

object debugKommand {
    private fun getInstance(): Plugin {
        return LianPlugin.instance
    }

    private fun getPrefix(path:String, sender: CommandSender) {
        val prefix = MainKommand.getInstance().config.getString(path)
        sender.sendMessage(Component.text("${ChatColor.GOLD}Debug |${ChatColor.WHITE} '${path}' 의 값은 '${prefix?.replace("&", "§")}' 입니다.")) } // 메시지

    fun debugKommand() {
        getInstance().kommand {
            register("debug") {
                permission("lian.debug") // 임시 퍼미션
                then("prefix") {
                    executes { sender.sendMessage(Component.text("대충 사용법 오류 메시지")) }
                    then("admin") { executes { getPrefix("admin_prefix", sender) } }
                    then("country") { executes { getPrefix("country_prefix", sender) } }
                    then("clan") { executes { getPrefix("clan_prefix", sender) } }
                    then("user") { executes { getPrefix("user_prefix", sender) } }
                }
            }
        }
    }
}