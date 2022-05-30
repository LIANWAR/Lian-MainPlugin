package com.lianserver.system.kommands

import com.lianserver.system.common.Ancients
import com.lianserver.system.interfaces.KommandInterface
import io.github.monun.kommand.Kommand.Companion.register
import io.github.monun.kommand.getValue
import io.github.monun.kommand.wrapper.BlockPosition3D
import org.bukkit.Location
import org.bukkit.entity.Player

class AncientsKommand: KommandInterface {
    fun BlockPosition3D.toLocation(p: Player) = Location(p.world, x.toDouble(), y.toDouble(), z.toDouble())

    override fun kommand(){
        register(getInstance(), "ancients"){
            requires { player.isOp }
            then("register"){
                then("pos" to blockPosition()){
                    executes {
                        val pos: BlockPosition3D by it

                        if(getInstance().ancientsData.none { it.value.pos == pos.toLocation(player) } && player.world.name == "world"){
                            getInstance().ancientsData[pos.toLocation(player).hashCode().toString()] = Ancients(
                                pos.toLocation(player),
                                null,
                                mutableMapOf()
                            )
                            player.sendMessage(adminText("유물을 등록했습니다."))
                        }
                        else {
                            player.sendMessage(adminText("유물이 이미 등록되어있습니다."))
                        }
                    }
                }
            }
            then("unregister"){
                then("pos" to blockPosition()){
                    executes {
                        val pos: BlockPosition3D by it

                        if(getInstance().ancientsData.any { it.value.pos == pos.toLocation(player) } && player.world.name == "world"){
                            getInstance().ancientsData.remove(pos.toLocation(player).hashCode().toString())
                            player.sendMessage(adminText("유물을 등록 해제했습니다."))
                        }
                        else {
                            player.sendMessage(adminText("유물이 없습니다."))
                        }
                    }
                }
            }
        }
    }

    init {}
}