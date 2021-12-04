package com.underconnor.lian.kommands

import com.underconnor.lian.clan.Clan
import com.underconnor.lian.plugin.KommandInterface
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

/***
 * @author underconnor, AlphaGot
 */

class ClanKommand: KommandInterface {
    private fun clanText(s: String): TextComponent = text("${getInstance().config.getString("clan_prefix")} $s")
    private fun clanTextS(s: String): String = "${getInstance().config.getString("clan_prefix")} $s"

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
                                    "    ${clanTextS("${ChatColor.WHITE}/clan leave")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan kick <플레이어>")}\n"
                        )
                    }
                }
                then("create") {
                    then("clanName" to string()) {
                        executes { kommandContext ->
                            if((sender as Player).inventory.itemInMainHand.displayName() == text("[클랜 창설권]", TextColor.color(1f, 1f, 0f))){
                                val clanNameTemp: String by kommandContext
                                clanNameTemp.replace("§", " ")
                                clanNameTemp.replace("&", " ")
                                clanNameTemp.replace("_", " ")
                                if(getInstance().getPlayer(sender).clan != null){
                                    sender.sendMessage(clanText("이미 클랜에 소속되어있습니다."))
                                }
                                else {
                                    if(getInstance().clans.none { it.name == clanNameTemp }) {
                                        val clan = Clan(name = clanNameTemp)
                                        getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().getPlayer(sender))].clan = clan
                                        clan.owner = getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().getPlayer(sender))]
                                        clan.players = arrayListOf(clan.owner)

                                        getInstance().server.broadcast(clanText("${sender.name}님이 ${ChatColor.GREEN}${clan.name}${ChatColor.WHITE}클랜을 생성했습니다."))
                                    }
                                    else {
                                        sender.sendMessage(clanText("이미 같은 이름의 클랜이 있습니다."))
                                    }
                                }
                            }
                            else {
                                sender.sendMessage(clanText("손에 클랜 창설권을 들어주세요."))
                            }
                        }
                    }
                    executes {
                        sender.sendMessage(clanText("클랜 이름을 입력해주세요."))
                    }
                }
                executes { sender.sendMessage(clanText("/clan help")) }
            }
        }
    }

    init {}
}