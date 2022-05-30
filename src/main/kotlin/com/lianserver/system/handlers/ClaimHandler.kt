package com.lianserver.system.handlers

import com.lianserver.system.common.Ancients
import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.common.Outpost
import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.StructureType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.absoluteValue

class ClaimHandler: HandlerInterface, PrefixedTextInterface {
    init {
        getInstance().server.scheduler.scheduleSyncRepeatingTask(
            getInstance(),
            {
                getInstance().server.onlinePlayers.filter { (getInstance().getPlayer(it).clan != null || getInstance().getPlayer(it).country != null) && it.location.world.name == "world" }.forEach {
                    val op = it.world.locateNearestStructure(it.location, StructureType.PILLAGER_OUTPOST, 1, false)

                    if(op != null){
						val loc = it.location.clone()
                        val loc2 = op.clone()
                        loc2.x += 8.0
                        loc2.z += 8.0
						loc.y = 0.0
                        if(loc2.distance(loc).absoluteValue <= 12.0){
                            if(getInstance().outpostData.containsKey(op.hashCode().toString())){
                                if(
                                    (getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId == getInstance().getPlayer(it).clan?.owner?.player?.uniqueId ||
                                            getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId == getInstance().getPlayer(it).country?.owner?.player?.uniqueId) &&
                                    getInstance().outpostData[op.hashCode().toString()]!!.ownedClan != null
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
                                        if(!getInstance().outpostData[op.hashCode().toString()]!!.claimPt.containsKey(getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString())) getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = 0
                                        getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]!! + 1

                                        it.playSound(
                                            Sound.sound(
                                                org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                                Sound.Source.PLAYER,
                                                1.0f,
                                                2.0f
                                            )
                                        )
                                        it.sendActionBar(Component.text("(${getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]}/600)"))

                                        if(getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]!! >= 600){
                                            getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = 0
                                            if (getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()) != null) {
                                                getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()).let {
                                                    if(it is Clan){
                                                        it.players.forEach {
                                                            if(it.player.isOnline){
                                                                val rit = it
                                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockZ})에 있는 전초기지가 다른 국가에게 점령되었습니다."))
                                                            }
                                                        }
                                                    }
                                                    if(it is Country){
                                                        it.players.forEach {
                                                            if(it.player.isOnline){
                                                                val rit = it
                                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(countryText("(${op.blockX}, ${op.blockZ})에 있는 전초기지가 다른 국가에게 점령되었습니다."))
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
                                                            false,
                                                            true,
                                                            true
                                                        )
                                                    )
                                                    getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(countryText("(${op.blockX}, ${op.blockZ})에 있는 전초기지를 점령했습니다."))
                                                }
                                            }
                                        }
                                    }
                                    else if(getInstance().getPlayer(it).clan != null){
                                        if(!getInstance().outpostData[op.hashCode().toString()]!!.claimPt.containsKey(getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString())) getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = 0
                                        getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]!! + 1

                                        it.playSound(
                                            Sound.sound(
                                                org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                                Sound.Source.PLAYER,
                                                1.0f,
                                                2.0f
                                            )
                                        )
                                        it.sendActionBar(Component.text("(${getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]}/600)"))

                                        if(getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]!! >= 600){
                                            getInstance().outpostData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = 0

                                            if (getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()) != null) {
                                                getInstance().getClanOrCountry(getInstance().outpostData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()).let {
                                                    if(it is Clan){
                                                        it.players.forEach {
                                                            if(it.player.isOnline){
                                                                val rit = it
                                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockZ})에 있는 전초기지가 다른 클랜에게 점령되었습니다."))
                                                            }
                                                        }
                                                    }
                                                    if(it is Country){
                                                        it.players.forEach {
                                                            if(it.player.isOnline){
                                                                val rit = it
                                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockZ})에 있는 전초기지가 다른 클랜에게 점령되었습니다."))
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
                                                    getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockZ})에 있는 전초기지를 점령했습니다."))
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
                }
            },
            0L,
            10L
        )

        getInstance().server.scheduler.scheduleSyncRepeatingTask(
            getInstance(),
            {
                getInstance().server.onlinePlayers.filter { (getInstance().getPlayer(it).clan != null || getInstance().getPlayer(it).country != null) && it.location.world.name == "world" }.forEach {
                    getInstance().ancientsData.values.forEach { git -> 
                        val op = git.pos
                        val loc = it.location.clone()
                        val loc2 = op.clone()
                        loc2.y = 0.0
                        loc.y = 0.0
                        if(loc2.distance(loc).absoluteValue <= 12.0){
                            if(
                                (getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId == getInstance().getPlayer(it).clan?.owner?.player?.uniqueId ||
                                        getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId == getInstance().getPlayer(it).country?.owner?.player?.uniqueId) &&
                                getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan != null
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
                                    if(!getInstance().ancientsData[op.hashCode().toString()]!!.claimPt.containsKey(getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString())) getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = 0
                                    getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]!! + 1

                                    it.playSound(
                                        Sound.sound(
                                            org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                            Sound.Source.PLAYER,
                                            1.0f,
                                            2.0f
                                        )
                                    )
                                    it.sendActionBar(Component.text("(${getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]}/600)"))

                                    if(getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()]!! >= 600){
                                        getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).country!!.owner.player.uniqueId.toString()] = 0
                                        if (getInstance().getClanOrCountry(getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()) != null) {
                                            getInstance().getClanOrCountry(getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()).let {
                                                if(it is Clan){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 유물이 다른 국가에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                                if(it is Country){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(countryText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 유물이 다른 국가에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan = getInstance().getPlayer(it).country!!
                                        getInstance().getPlayer(it).country!!.players.forEach {
                                            if(it.player.isOnline){
                                                val rit = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(countryText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 유물을 점령했습니다."))
                                            }
                                        }
                                    }
                                }
                                else if(getInstance().getPlayer(it).clan != null){
                                    if(!getInstance().ancientsData[op.hashCode().toString()]!!.claimPt.containsKey(getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString())) getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = 0
                                    getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]!! + 1

                                    it.playSound(
                                        Sound.sound(
                                            org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                                            Sound.Source.PLAYER,
                                            1.0f,
                                            2.0f
                                        )
                                    )
                                    it.sendActionBar(Component.text("(${getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]}/600)"))

                                    if(getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()]!! >= 600){
                                        getInstance().ancientsData[op.hashCode().toString()]!!.claimPt[getInstance().getPlayer(it).clan!!.owner.player.uniqueId.toString()] = 0

                                        if (getInstance().getClanOrCountry(getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()) != null) {
                                            getInstance().getClanOrCountry(getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan?.owner?.player?.uniqueId.toString()).let {
                                                if(it is Clan){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 유물이 다른 클랜에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                                if(it is Country){
                                                    it.players.forEach {
                                                        if(it.player.isOnline){
                                                            val rit = it
                                                            getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 유물이 다른 클랜에게 점령되었습니다."))
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        getInstance().ancientsData[op.hashCode().toString()]!!.ownedClan = getInstance().getPlayer(it).clan!!

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
                                                getInstance().server.onlinePlayers.first { it.uniqueId == rit.player.uniqueId }.sendMessage(clanText("(${op.blockX}, ${op.blockY}, ${op.blockZ})에 있는 유물을 점령했습니다."))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            0L,
            10L
        )
    }
}