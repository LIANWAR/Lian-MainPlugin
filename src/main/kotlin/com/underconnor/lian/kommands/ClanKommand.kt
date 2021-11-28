package com.underconnor.lian.kommands

import com.underconnor.lian.plugin.KommandInterface
import com.underconnor.lian.plugin.LianPlugin
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.md_5.bungee.api.ChatColor
import org.bukkit.plugin.Plugin

/***
 * @author underconnor, AlphaGot
 */

class ClanKommand: KommandInterface {
    fun clanText(s: String): TextComponent = text("${getInstance().config.getString("clan_prefix")} $s")
    fun clanTextS(s: String): String = "${getInstance().config.getString("clan_prefix")} $s"

    override fun kommand() {
        getInstance().kommand {
            register("clan") {
                then("help"){
                    executes {
                        sender.sendMessage(
                                    "${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}- 클랜 도움말 ${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan info")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan create <클랜 이름>")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan invite <플레이어>")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan chat")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan leave")}\n"
                        )
                    }
                }
                executes { sender.sendMessage(clanText("/clan help")) }
            }
        }
    }

    init {}
}