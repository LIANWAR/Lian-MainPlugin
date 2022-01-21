package com.lianserver.system.handlers

import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
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
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country && !e.player.isOp){
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
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country && !e.player.isOp){
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
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country && !e.player.isOp){
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
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second != getInstance().getPlayer(e.player).country && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun claimHandler(e: PlayerInteractEvent){
        if(e.clickedBlock != null){
            if(e.item?.type == Material.WRITTEN_BOOK){
                if(e.item!!.hasItemMeta()){
                    if(e.item!!.itemMeta.displayName() == text("땅문서").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)){
                        var cond = true
                        val tT: MutableList<Pair<Clan?, Country?>> = mutableListOf()
                        for(nX in -1..2){
                            for(nZ in -1..2){
                                cond = cond && (getInstance().getLandOwned(Pair<Int, Int>(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ)) == Pair<Clan?, Country?>(null, null))
                                tT.add(getInstance().getLandOwned(Pair<Int, Int>(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ)))
                            }
                        }

                        if(cond){
                            if(getInstance().getPlayer(e.player).clan != null || getInstance().getPlayer(e.player).country != null){
                                if(getInstance().getPlayer(e.player).clan != null){
                                    if(getInstance().getPlayer(e.player).clan!!.land != Pair<Int?, Int?>(null, null)){
                                        e.player.sendMessage(clanText("이미 클랜이 땅을 갖고 있습니다."))
                                    }
                                    else if(getInstance().getPlayer(e.player).clan!!.owner.player.uniqueId != e.player.uniqueId){
                                        e.player.sendMessage(clanText("클랜장이 아닙니다."))
                                    }
                                    else {
                                        getInstance().clans[e.player.uniqueId.toString()]!!.land = Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)
                                        e.player.sendMessage(clanText("클랜의 땅을 설정했습니다."))
                                    }
                                }
                                else {
                                    if(getInstance().getPlayer(e.player).country!!.land != Pair<Int?, Int?>(null, null)){
                                        e.player.sendMessage(countryText("이미 국가이 땅을 갖고 있습니다."))
                                    }
                                    else if(getInstance().getPlayer(e.player).country!!.owner.player.uniqueId != e.player.uniqueId){
                                        e.player.sendMessage(countryText("수령이 아닙니다."))
                                    }
                                    else {
                                        getInstance().countries[e.player.uniqueId.toString()]!!.land = Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)
                                        e.player.sendMessage(countryText("국가의 땅을 설정했습니다."))
                                    }
                                }
                            }
                            else {
                                e.player.sendMessage(countryText("클랜 또는 국가가 없습니다."))
                            }
                        }
                        else {
                            e.player.sendMessage(clanText("소유하려는 땅 및 그 주변이 다음 클랜 또는 국가의 땅입니다."))
                            tT.forEach {
                                if(it.first != null){
                                    e.player.sendMessage(clanText(" - ${it.first!!.name} 클랜"))
                                }
                                else {
                                    e.player.sendMessage(clanText(" - ${it.second!!.name} 국가"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    init {}
}