package com.lianserver.system.handlers

import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

class LandHandler: HandlerInterface, PrefixedTextInterface {
    @EventHandler
    fun onPlace(e: BlockPlaceEvent){
        if(getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country){
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun onPlaceM(e: BlockMultiPlaceEvent){
        if(getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country){
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent){
        if(getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country){
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent){
        if(getInstance().getLandOwned(Pair(e.clickedBlock?.chunk?.x, e.clickedBlock?.chunk?.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.clickedBlock?.chunk?.x, e.clickedBlock?.chunk?.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country){
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    init {}
}