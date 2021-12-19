package com.underconnor.lian.handlers

import com.underconnor.lian.common.Land
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
        val bArr = getInstance().lands.filter {
            arrayOf(it.key.first - 1, it.key.first, it.key.first + 1).contains(e.block.chunk.x) && arrayOf(it.key.second - 1, it.key.second, it.key.second + 1).contains(e.block.chunk.z)
        }.map {
            it.value
        }

        if(bArr.isNotEmpty()){
            val here = bArr[0]

            if(!here.allows.contains(getInstance().getPlayer(e.player))){
                e.isCancelled = true
                e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다. (땅 주인: ${here.originalOwner.player.name})")
            }
        }
    }

    @EventHandler
    fun onPlaceM(e: BlockMultiPlaceEvent){
        val bArr = getInstance().lands.filter {
            arrayOf(it.key.first - 1, it.key.first, it.key.first + 1).contains(e.block.chunk.x) && arrayOf(it.key.second - 1, it.key.second, it.key.second + 1).contains(e.block.chunk.z)
        }.map {
            it.value
        }

        if(bArr.isNotEmpty()){
            val here = bArr[0]

            if(!here.allows.contains(getInstance().getPlayer(e.player))){
                e.isCancelled = true
                e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다. (땅 주인: ${here.originalOwner.player.name})")
            }
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent){
        if(getInstance().getLand(Pair(e.block.chunk.x, e.block.chunk.z)) != null){
            val here = getInstance().getLand(Pair(e.block.chunk.x, e.block.chunk.z))

            if(!here!!.allows.contains(getInstance().getPlayer(e.player))){
                e.isCancelled = true
                e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다. (땅 주인: ${here.originalOwner.player.name})")
            }
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent){
        if (e.clickedBlock != null) {
            if(e.item?.type == Material.ENCHANTED_BOOK && e.item?.hasItemMeta() == true){
                if(e.item!!.itemMeta.hasDisplayName()){
                    if(e.item!!.itemMeta.displayName() == text("땅문서").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)){
                        if(getInstance().getPlayer(e.player).ownedLand == null && getInstance().getPlayer(e.player).clan != null){
                            var already = 0
                            var otherUser = 0
                            e.isCancelled = true

                            for (nX in -1..1){
                                for (nZ in -1..1){
                                    if(getInstance().getLand(Pair(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ)) != null){
                                        val here = getInstance().getLand(Pair(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ))

                                        if(!here!!.allows.contains(getInstance().getPlayer(e.player))){
                                            e.player.sendMessage(countryText("다른 사람의 땅과 겹쳐서 클레임할 수 없습니다. (땅 주인: ${here.originalOwner})"))
                                            return
                                        }
                                        else {
                                            already++
                                        }
                                    }
                                    else {
                                        getInstance().lands[Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)] = Land(
                                            getInstance().getPlayer(e.player),
                                            pos = Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)
                                        )
                                        getInstance().onlinePlayers[e.player.uniqueId.toString()]!!.ownedLand = getInstance().lands[Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)]!!
                                    }
                                }
                            }

                            e.player.sendMessage(countryText(
                                """
                                    청크 (${e.clickedBlock!!.chunk.x - 1}, ${e.clickedBlock!!.chunk.z - 1})부터 (${e.clickedBlock!!.chunk.x + 1}, ${e.clickedBlock!!.chunk.z + 1}) 범위의 9청크를 클레임했습니다.
                                """.trimIndent()))

                            e.item!!.subtract(1)
                        }
                        else {
                            if(getInstance().getPlayer(e.player).clan == null){
                                e.player.sendMessage(countryText("클랜에 가입되어있어야만 땅을 클레임할 수 있습니다."))
                            }
                            else {
                                e.player.sendMessage(countryText("땅을 이미 갖고 있습니다."))
                            }
                        }
                    }
                }
            }
            else {
                val bArr = getInstance().lands.filter {
                    arrayOf(it.key.first - 1, it.key.first, it.key.first + 1).contains(e.clickedBlock!!.chunk.x) && arrayOf(it.key.second - 1, it.key.second, it.key.second + 1).contains(e.clickedBlock!!.chunk.z)
                }.map {
                    it.value
                }

                if(bArr.isNotEmpty()){
                    val here = bArr[0]

                    if(!here.allows.contains(getInstance().getPlayer(e.player))){
                        e.isCancelled = true
                        e.player.sendMessage("[${ChatColor.DARK_GREEN}!${ChatColor.RESET}] 땅 주인이 아닙니다. (땅 주인: ${here.originalOwner.player.name})")
                    }
                }
            }
        }
    }
    
    init {}
}