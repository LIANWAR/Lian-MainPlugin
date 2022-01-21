package com.lianserver.system.handlers

import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
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
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

class WarHandler: HandlerInterface, PrefixedTextInterface {
    @EventHandler
    fun onHit(e: PlayerInteractAtEntityEvent){
		getInstance().logger.info("asdf")
        if(e.rightClicked is ArmorStand){
			getInstance().logger.info(e.rightClicked.toString())
            if(e.rightClicked.scoreboardTags.contains("lian_flag")){
                val fw = e.rightClicked.world.spawnEntity(e.rightClicked.location.add(0.0, 0.2, 0.0), EntityType.FIREWORK) as Firework

                val meta = fw.fireworkMeta
                meta.power = 2
                meta.addEffect(
                    FireworkEffect.builder().withColor(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE).flicker(true).build()
                )

                fw.fireworkMeta = meta
                fw.detonate()
                (e.rightClicked as ArmorStand).damage(Double.MAX_VALUE)
            }
        }
    }

    init {}
}