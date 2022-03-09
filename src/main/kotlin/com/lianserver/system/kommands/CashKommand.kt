package com.lianserver.system.kommands

import com.lianserver.system.interfaces.KommandInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.kommand.Kommand.Companion.register
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

class CashKommand: KommandInterface, PrefixedTextInterface {
    override fun kommand() {
        register(getInstance(), "cash"){
            requires { player.isOp }

            executes { player.sendMessage(adminText("/cash help")) }
            then("help") {
                executes {
                    player.sendMessage(
                        "${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}- 후원 관리 도움말 ${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-${ChatColor.WHITE}-${ChatColor.GOLD}-\n" +
                                "${ChatColor.RESET} - ${adminTextS("${ChatColor.WHITE}/cash show <플레이어>: <플레이어>가 소유한 캐시를 보여줍니다.")}\n" +
                                " - ${adminTextS("${ChatColor.WHITE}/cash give <플레이어> <액수>: <플레이어>에게 <액수>만큼의 캐시를 지급합니다.")}\n" +
                                " - ${adminTextS("${ChatColor.WHITE}/cash reset <플레이어>: <플레이어>의 캐시를 초기화합니다.")}\n"
                    )
                }
            }
            then("show"){
                executes { player.sendMessage(adminText("/cash help")) }
                then("play" to player()){
                    executes {
                        val play: Player by it
                        val lP = getInstance().getPlayer(play)

                        player.sendMessage(adminText("").append(play.displayName()).append(Component.text("님이 소유한 캐시: ${lP.cash}")))
                    }
                }
            }
            then("reset"){
                executes { player.sendMessage(adminText("/cash help")) }
                then("play" to player()){
                    executes {
                        val play: Player by it

                        getInstance().onlinePlayers[play.uniqueId.toString()]!!.cash = 0
                        player.sendMessage(adminText("").append(play.displayName()).append(Component.text("님의 캐시를 0원으로 초기화했습니다.")))
                    }
                }
            }
            then("give"){
                executes { player.sendMessage(adminText("/cash help")) }
                then("play" to player()){
                    executes { player.sendMessage(adminText("/cash help")) }
                    then("amt" to int(0, Integer.MAX_VALUE)){
                        executes {
                            val play: Player by it
                            val amt: Int by it

                            getInstance().onlinePlayers[play.uniqueId.toString()]!!.cash += amt
                            player.sendMessage(adminText("").append(play.displayName()).append(Component.text("님에게 $amt 캐시를 지급했습니다. (현재 ${getInstance().onlinePlayers[play.uniqueId.toString()]!!.cash})")))
                        }
                    }
                }
            }
        }
    }

    init {}
}