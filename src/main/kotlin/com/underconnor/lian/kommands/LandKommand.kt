package com.underconnor.lian.kommands

import com.underconnor.lian.plugin.KommandInterface
import com.underconnor.lian.plugin.LianPlugin
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/***
 * @author AlphaGot
 */

class LandKommand: KommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("land") {
                executes {
                    sender.sendMessage(countryText("/land help"))
                }
                then("reset"){
                    executes {
                        val player = getInstance().getPlayer(sender)

                        getInstance().lands.remove(getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand!!.loc)
                        getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand = null

                        sender.sendMessage(countryText("땅을 초기화했습니다."))
                    }
                }
                then("trust"){
                    executes {
                        sender.sendMessage(countryText("누구를 허가할 지를 입력해주세요."))
                    }
                    then("trustee" to player()){
                        executes {
                            val player = getInstance().getPlayer(sender)
                            val trustee: Player by it
                            val trusteeR = getInstance().getPlayer(trustee)

                            if(player.ownedLand == null){
                                sender.sendMessage(countryText("땅이 없습니다."))
                            }
                            else {
                                if(player.ownedLand!!.allows.contains(trusteeR)){
                                    sender.sendMessage(countryText("해당 플레이어가 이미 허가되어있습니다."))
                                }
                                else {
                                    getInstance().lands[getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand!!.loc]!!.allows.plusAssign(trusteeR)
                                    getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand = getInstance().lands[getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand!!.loc]!!

                                    sender.sendMessage(countryText("${trustee.name}님을 허가했습니다."))
                                }
                            }
                        }
                    }
                }
                then("untrust"){
                    executes {
                        sender.sendMessage(countryText("누구를 허가 취소할 지를 입력해주세요."))
                    }
                    then("trustee" to player()){
                        executes {
                            val player = getInstance().getPlayer(sender)
                            val trustee: Player by it
                            val trusteeR = getInstance().getPlayer(trustee)

                            if(player.ownedLand == null){
                                sender.sendMessage(countryText("땅이 없습니다."))
                            }
                            else {
                                if(!player.ownedLand!!.allows.contains(trusteeR)){
                                    sender.sendMessage(countryText("해당 플레이어가 허가되어있지 않습니다."))
                                }
                                else {
                                    getInstance().lands[getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand!!.loc]!!.allows.remove(trusteeR)
                                    getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand = getInstance().lands[getInstance().onlinePlayers[player.player.uniqueId.toString()]!!.ownedLand!!.loc]!!

                                    sender.sendMessage(countryText("${trustee.name}님을 허가 취소했습니다."))
                                }
                            }
                        }
                    }
                }
                then("help"){
                    executes {
                        sender.sendMessage(
                            "${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}- 땅 도움말 ${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/land help")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/land reset")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/land trust <플레이어>")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/land untrust <플레이어>")}\n"
                        )
                    }
                }
            }
        }
    }

    init {}
}