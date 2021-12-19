package com.underconnor.lian.kommands

import com.underconnor.lian.plugin.KommandInterface
import com.underconnor.lian.plugin.LianPlugin
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/***
 * @author underconnor, AlphaGot
 */

class AdminKommand: KommandInterface {

    override fun kommand() {
        getInstance().kommand {
            register("resetLandForce") {
                executes {
                    sender.sendMessage(adminText("초기화할 플레이어를 입력해주세요."))
                }
                then("player" to player()){
                    executes {
                        if(sender.isOp){
                            val player: Player by it

                            if(getInstance().getPlayer(player).ownedLand == null){
                                sender.sendMessage(adminText("해당 플레이어가 땅을 소유하고 있지 않습니다."))
                            }
                            else {
                                getInstance().lands.remove(getInstance().onlinePlayers[player.uniqueId.toString()]!!.ownedLand!!.loc)
                                getInstance().onlinePlayers[player.uniqueId.toString()]!!.ownedLand = null
                            }
                        }
                    }
                }
            }
        }
    }

    init {}
}