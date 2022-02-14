package com.lianserver.system.handlers

import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.papermc.paper.event.entity.EntityMoveEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class WarHandler: HandlerInterface, PrefixedTextInterface {
    @EventHandler
    fun onHit(e: PlayerInteractAtEntityEvent){
        if(e.rightClicked is ArmorStand){
            if(e.rightClicked.scoreboardTags.contains("#lian_flag")){
                if(getInstance().getPlayer(e.player).country != null){
                    val tag = e.rightClicked.scoreboardTags.sorted()
                    if(arrayOf(
                            Pair(getInstance().getPlayer(e.player).country!!, getInstance().countries[tag[1]]!!),
                            Pair(getInstance().countries[tag[1]]!!, getInstance().getPlayer(e.player).country!!)
                        ).contains(getInstance().getWar(getInstance().getPlayer(e.player).country!!.owner.player.uniqueId.toString())?.countries)){
                        val fw = e.rightClicked.world.spawnEntity(e.rightClicked.location.add(0.0, 0.2, 0.0), EntityType.FIREWORK) as Firework

                        val meta = fw.fireworkMeta
                        meta.power = 2
                        meta.addEffect(
                            FireworkEffect.builder().withColor(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE).flicker(true).with(FireworkEffect.Type.BALL_LARGE).build()
                        )

                        fw.fireworkMeta = meta
                        fw.detonate()
                        (e.rightClicked as ArmorStand).remove()
                        
                        val w = getInstance().getWar(getInstance().getPlayer(e.player).country!!.owner.player.uniqueId.toString())
                        val t = e.rightClicked.scoreboardTags.elementAt(1)
                        
                        if(w == null){
                            return
                        }
                        
                        if(w.countries.first.owner.player.uniqueId.toString() == t){
                            w.countries.first.players.forEach { 
                                if(it.player.isOnline){
                                    val i = it
                                    
                                    getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.RED}전쟁 패배", "${ChatColor.DARK_GRAY}우리의 땅을 잃었습니다.", 25, 75, 20)
                                }
                            }

                            w.countries.second.players.forEach {
                                if(it.player.isOnline){
                                    val i = it

                                    getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.AQUA}전쟁 승리", "${ChatColor.DARK_GRAY}국가가 또 하나의 승리를 이루어냈습니다.", 25, 75, 20)
                                }
                            }

                            getInstance().countries[w.countries.first.owner.player.uniqueId.toString()]!!.land = null
                            getInstance().countries[w.countries.second.owner.player.uniqueId.toString()]!!.winCount++
                            getInstance().getFlagArmorStand(w.countries.second.owner.player.uniqueId.toString())?.isGlowing = false
                        }
                        else {
                            w.countries.second.players.forEach {
                                if(it.player.isOnline){
                                    val i = it

                                    getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.RED}전쟁 패배", "${ChatColor.DARK_GRAY}우리의 땅을 잃었습니다.", 25, 75, 20)
                                }
                            }

                            w.countries.first.players.forEach {
                                if(it.player.isOnline){
                                    val i = it

                                    getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.AQUA}전쟁 승리", "${ChatColor.DARK_GRAY}국가가 또 하나의 승리를 이루어냈습니다.", 25, 75, 20)
                                }
                            }

                            getInstance().countries[w.countries.second.owner.player.uniqueId.toString()]!!.land = null
                            getInstance().countries[w.countries.first.owner.player.uniqueId.toString()]!!.winCount++
                            getInstance().getFlagArmorStand(w.countries.first.owner.player.uniqueId.toString())?.isGlowing = false
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onFlagMove(e: EntityMoveEvent){
        if(e.entityType == EntityType.ARMOR_STAND){
            if(e.entity.scoreboardTags.contains("#lian_flag")){
                e.isCancelled = true
            }
        }
    }

    init {}
}