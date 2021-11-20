package com.underconnor.lian.kommands

import com.underconnor.lian.plugin.KommandInterface
import com.underconnor.lian.plugin.LianPlugin
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import org.bukkit.plugin.Plugin

/***
 * @author underconnor, AlphaGot
 */

object MainKommand: KommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("LianPlugin") {
                executes { sender.sendMessage(text("대충 소개 메시지")) }
            }
        }
    }
}