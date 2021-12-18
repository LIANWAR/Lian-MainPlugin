package com.underconnor.lian.kommands

import com.underconnor.lian.plugin.DebugKommandInterface
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class debugKommand: DebugKommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("debug") {
                requires { player.isOp }
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

    init {}
}