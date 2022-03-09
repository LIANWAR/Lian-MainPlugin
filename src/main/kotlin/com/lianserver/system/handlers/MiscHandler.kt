package com.lianserver.system.handlers

import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class MiscHandler: HandlerInterface, PrefixedTextInterface {
    @EventHandler
    fun onLottoUse(e: PlayerSwapHandItemsEvent){
        if(e.mainHandItem?.type == Material.PAPER || e.offHandItem?.type == Material.PAPER){
            if(e.mainHandItem?.type == Material.PAPER){
                val item = e.mainHandItem!!

                if(item.hasItemMeta()){
                    if(item.itemMeta.displayName() == text("로또").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)){
                        e.mainHandItem!!.subtract(1)

                        val stack = when(Random.nextInt(0, 1000)){
                            in 0..9 -> ItemStack(Material.NETHERITE_INGOT)
                            in 10..39 -> ItemStack(Material.DIAMOND, 8)
                            in 40..399 -> ItemStack(Material.DIAMOND)
                            else -> ItemStack(Material.AIR)
                        }

                        e.player.inventory.addItem(stack)

                        if(stack.type == Material.AIR){
                            e.player.sendMessage(userText("꽝!"))
                        }
                        else {
                            e.player.sendMessage(
                                when(stack){
                                    ItemStack(Material.NETHERITE_INGOT) -> userText("1등 당첨!")
                                    ItemStack(Material.DIAMOND, 8) -> userText("2등 당첨!")
                                    ItemStack(Material.DIAMOND, 1) -> userText("3등 당첨!")
                                    else -> userText("버그 당첨!")
                                }
                            )
                        }
                    }
                }
            }
            else {
                val item = e.offHandItem!!

                if(item.hasItemMeta()){
                    if(item.itemMeta.displayName() == text("로또").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)){
                        e.offHandItem!!.subtract(1)

                        val stack = when(Random.nextInt(0, 1000)){
                            in 0..9 -> ItemStack(Material.NETHERITE_INGOT)
                            in 10..39 -> ItemStack(Material.DIAMOND, 8)
                            in 40..399 -> ItemStack(Material.DIAMOND)
                            else -> ItemStack(Material.AIR)
                        }

                        e.player.inventory.addItem(stack)

                        if(stack.type == Material.AIR){
                            e.player.sendMessage(userText("꽝!"))
                        }
                        else {
                            e.player.sendMessage(
                                when(stack){
                                    ItemStack(Material.NETHERITE_INGOT) -> userText("1등 당첨!")
                                    ItemStack(Material.DIAMOND, 8) -> userText("2등 당첨!")
                                    ItemStack(Material.DIAMOND, 1) -> userText("3등 당첨!")
                                    else -> userText("버그 당첨!")
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    init {}
}