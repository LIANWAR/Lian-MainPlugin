package com.lianserver.system.handlers

import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.common.Outpost
import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import org.bukkit.StructureType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ClaimHandler: HandlerInterface, PrefixedTextInterface {
    init {
        getInstance().server.scheduler.scheduleSyncRepeatingTask(
            getInstance(),
            {
                getInstance().server.onlinePlayers.filter { getInstance().getPlayer(it).clan != null || getInstance().getPlayer(it).country != null }.forEach {
                    val op = it.world.locateNearestStructure(it.location, StructureType.PILLAGER_OUTPOST, 18, false)

                    if(op != null){
                        if(getInstance().outpostData.containsKey(op.hashCode().toString())){
                            if(
                                getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId == getInstance().getPlayer(it).clan!!.owner.player.uniqueId ||
                                getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId == getInstance().getPlayer(it).country!!.owner.player.uniqueId
                            ){
                                it.addPotionEffect(PotionEffect(
                                    PotionEffectType.REGENERATION,
                                    11,
                                    2,
                                    true,
                                    false,
                                    true
                                ))
                            }
                            else {
                                if(getInstance().getPlayer(it).country != null){
                                    getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]!! + 1

                                    if(getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]!! >= 600){
                                        getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = 0
                                        if (getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()) != null) {
                                            getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()).let {
                                                if(it is Clan){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 전초기지가 다른 국가에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                                if(it is Country){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(countryText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 전초기지가 다른 국가에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        getInstance().outpostData[op.hashCode().toString()]!!.ownedClan = getInstance().getPlayer(it).country!!
                                        getInstance().getPlayer(it).country!!.players.forEach {
                                            if(it.player.isOnline){
                                                val rit = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.addPotionEffect(
                                                    PotionEffect(
                                                        PotionEffectType.BAD_OMEN,
                                                        20000000,
                                                        1,
                                                        true,
                                                        true,
                                                        true
                                                    )
                                                )
                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(countryText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 전초기지를 점령했습니다."))
                                            }
                                        }
                                    }
                                }
                                else if(getInstance().getPlayer(it).clan != null){
                                    getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]!! + 1

                                    if(getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]!! >= 600){
                                        getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = 0

                                        if (getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()) != null) {
                                            getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()).let {
                                                if(it is Clan){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 전초기지가 다른 클랜에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                                if(it is Country){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 전초기지가 다른 클랜에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        getInstance().outpostData[op.hashCode().toString()]!!.ownedClan = getInstance().getPlayer(it).clan!!

                                        getInstance().getPlayer(it).clan!!.players.forEach {
                                            if(it.player.isOnline){
                                                val rit = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.addPotionEffect(
                                                    PotionEffect(
                                                        PotionEffectType.BAD_OMEN,
                                                        20000000,
                                                        1,
                                                        true,
                                                        true,
                                                        true
                                                    )
                                                )
                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 전초기지를 점령했습니다."))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            getInstance().outpostData[op.hashCode().toString()] = Outpost(
                                op,
                                null,
                                mutableMapOf<String, Int>()
                            )
                        }
                    }
                }
            },
            0L,
            10L
        )
    }
}