package com.underconnor.lian.handlers

import com.underconnor.lian.plugin.HandlerInterface
import com.underconnor.lian.plugin.PrefixedTextInterface
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
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
        val cond = getInstance().onlinePlayers.filterNot { it.value.player.uniqueId == e.player.uniqueId }.map { entry ->
            entry.value.ownedLands.none {
                Pair(e.block.chunk.x, e.block.chunk.z) == it
            }
        }

        var cache = true
        cond.forEach {
            cache = cache && it
        }

        e.isCancelled = !cache
        if(!cache){
            e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다.")
        }
    }

    @EventHandler
    fun onPlaceM(e: BlockMultiPlaceEvent){
        val cond = getInstance().onlinePlayers.filterNot { it.value.player.uniqueId == e.player.uniqueId }.map { entry ->
            entry.value.ownedLands.none {
                Pair(e.block.chunk.x, e.block.chunk.z) == it
            }
        }

        var cache = true
        cond.forEach {
            cache = cache && it
        }

        e.isCancelled = !cache
        if(!cache){
            e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다.")
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent){
        val cond = getInstance().onlinePlayers.filterNot { it.value.player.uniqueId == e.player.uniqueId }.map { entry ->
            entry.value.ownedLands.none {
                Pair(e.block.chunk.x, e.block.chunk.z) == it
            }
        }

        var cache = true
        cond.forEach {
            cache = cache && it
        }

        e.isCancelled = !cache
        if(!cache){
            e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다.")
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent){
        if (e.clickedBlock != null) {
            if(e.item?.type == Material.ENCHANTED_BOOK && e.item?.hasItemMeta() == true){
                if(e.item!!.itemMeta.hasDisplayName()){
                    if(e.item!!.itemMeta.displayName() == text("땅문서").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)){
                        var cond = listOf<Boolean>()
                        var ca: Boolean = true
                        for(x in -1..1){
                            for(z in -1..1){
                                val cc = getInstance().getPlayer(e.player).ownedLands.none {
                                    Pair(e.clickedBlock!!.chunk.x + x, e.clickedBlock!!.chunk.z + z) == it
                                }

                                ca = ca && !cc
                                if(cc) {
                                    cond = getInstance().onlinePlayers.map { entry ->
                                        entry.value.ownedLands.none {
                                            Pair(e.clickedBlock!!.chunk.x + x, e.clickedBlock!!.chunk.z + z) == it
                                        }
                                    }
                                }
                            }
                        }

                        if(ca){
                            e.player.sendMessage(countryText("해당 땅은 이미 소유한 땅입니다."))
                        }

                        var cache = true
                        cond.forEach {
                            cache = cache && it
                        }

                        if(!cache){
                            e.player.sendMessage(countryText("해당 땅들은 이미 다른 플레이어가 소유한 땅입니다."))
                        }
                        else {
                            for(nX in -1..1){
                                for(nZ in -1..1){
                                    getInstance().onlinePlayers[e.player.uniqueId.toString()]!!.ownedLands = getInstance().onlinePlayers[e.player.uniqueId.toString()]!!.ownedLands.plusElement(Pair(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ))
                                }
                            }
                            e.player.sendMessage(countryText("땅을 클레임했습니다."))
                            e.item!!.subtract(1)
                        }
                    }
                }
            }
            else {
                val cond = getInstance().onlinePlayers.filterNot { it.value.player.uniqueId == e.player.uniqueId }.map { entry ->
                    entry.value.ownedLands.none {
                        Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z) == it
                    }
                }

                var cache = true
                cond.forEach {
                    cache = cache && it
                }

                e.isCancelled = !cache
                if(!cache){
                    e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다.")
                }
            }
        }
    }
    
    init {}
}