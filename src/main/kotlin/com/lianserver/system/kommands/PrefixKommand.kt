package com.lianserver.system.kommands

import com.lianserver.system.interfaces.KommandInterface
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class PrefixKommand: KommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("prefix") {
                requires { player.isOp }
                then("set") {
                    executes { sender.sendMessage(text("플레이어와 칭호 인자가 필요합니다.")) }
                    then("p" to player()) {
                        executes { sender.sendMessage(text("칭호 인자가 필요합니다.")) }
                        then("prefixRaw" to string(StringType.GREEDY_PHRASE)){
                            executes {
                                val p: Player by it
                                val prefixRaw: String by it

                                val prefix = prefixRaw.replace('&', ChatColor.COLOR_CHAR)

                                p.displayName(text(prefix).append(text(" ")).append(p.name()))

                                getInstance().onlinePlayers[p.uniqueId.toString()]!!.prefix = prefix

                                sender.sendMessage("칭호를 설정했습니다.")
                            }
                        }
                    }
                }
                then("reset") {
                    executes { sender.sendMessage(text("플레이어와 칭호 인자가 필요합니다.")) }
                    then("p" to player()) {
                        executes {
                            val p: Player by it

                            p.displayName(p.name())

                            getInstance().onlinePlayers[p.uniqueId.toString()]!!.prefix = "**null**"

                            sender.sendMessage("칭호를 초기화했습니다.")
                        }
                    }
                }
            }
        }
    }

    init {}
}