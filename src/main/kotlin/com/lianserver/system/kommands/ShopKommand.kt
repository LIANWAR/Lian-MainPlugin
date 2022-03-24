package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.lianserver.system.interfaces.KommandInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.kommand.Kommand.Companion.register
import io.github.monun.kommand.getValue
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ShopKommand: KommandInterface, PrefixedTextInterface {

    @Suppress("UNCHECKED_CAST")
    override fun kommand() {
        register(getInstance(), "cshop", "캐시상점"){
            executes {
                val guiCashShop = ChestGui(4, "캐시 상점")
                guiCashShop.setOnGlobalClick { e: InventoryClickEvent ->
                    e.isCancelled = true
                }
                val paneItem = PaginatedPane(1, 1, 7, 2)
                paneItem.populateWithGuiItems(
                    getInstance().cashShopItem.map {
                        val itemStack = (it.get("item") as ItemStack).clone()

                        val lore = (itemStack.lore() ?: listOf<Component>()).toMutableList()

                        if(lore.isNotEmpty()) lore.add(text(""))
                        lore.add(text("${ChatColor.AQUA}가격: ${ChatColor.YELLOW}${it.get("price") as Int}캐시"))

                        itemStack.lore(lore)

                        GuiItem(
                            itemStack
                        ){ e: InventoryClickEvent ->
                            e.isCancelled = true

                            val pl = getInstance().getPlayer(e.whoClicked as Player)
                            if(pl.cash < it.get("price") as Int){
                                e.whoClicked.sendMessage(userText("캐시가 모자랍니다."))
                            }
                            else {
                                getInstance().onlinePlayers[e.whoClicked.uniqueId.toString()]!!.cash -= it.get("price") as Int
                                e.whoClicked.sendMessage(userText("").append(itemStack.displayName()).append(Component.text(" 아이템을 ${it.get("price") as Int}캐시에 구입했습니다.")))

                                e.whoClicked.inventory.addItem(
                                    (it.get("item") as ItemStack)
                                )
                            }
                        }
                    }
                )
                val navigation = StaticPane(1, 3, 7, 1)

                val rw = ItemStack(Material.RED_WOOL)
                var meta = rw.itemMeta
                meta.displayName(text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                rw.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        rw
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        if (paneItem.page > 0) {
                            paneItem.page = paneItem.page - 1
                            guiCashShop.update()
                        }
                    }, 0, 0
                )

                val gw = ItemStack(Material.GREEN_WOOL)
                meta = gw.itemMeta
                meta.displayName(Component.text("다음").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                gw.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        gw
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        if (paneItem.page < paneItem.pages - 1) {
                            paneItem.page = paneItem.page + 1
                            guiCashShop.update()
                        }
                    }, 6, 0
                )

                val br = ItemStack(Material.BARRIER)
                meta = br.itemMeta
                meta.displayName(Component.text("닫기").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
                br.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        br
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        event.whoClicked.closeInventory()
                    }, 3, 0
                )

                guiCashShop.addPane(navigation)
                val background = OutlinePane(0, 0, 9, 4)
                val stack = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
                meta = stack.itemMeta
                meta.displayName(text(""))
                stack.itemMeta = meta

                background.addItem(GuiItem(stack))
                background.setRepeat(true)
                background.priority = Pane.Priority.LOWEST
                background.setOnClick { event: InventoryClickEvent ->
                    event.isCancelled = true
                }
                guiCashShop.addPane(paneItem)
                guiCashShop.addPane(background)
				
				guiCashShop.update()
                guiCashShop.show(player)
            }
        }
        register(getInstance(), "ushop", "유저상점"){
            executes {
                val guiCashShop = ChestGui(4, "유저 상점")
                guiCashShop.setOnGlobalClick { e: InventoryClickEvent ->
                    e.isCancelled = true
                }
                val paneItem = PaginatedPane(1, 1, 7, 2)
                paneItem.populateWithGuiItems(
                    getInstance().userShopItem.map {
                        val itemStack = (it.get("item") as ItemStack).clone()

                        val lore = (itemStack.lore() ?: listOf<Component>()).toMutableList()

                        if(lore.isNotEmpty()) lore.add(text(""))
                        lore.add(text("${ChatColor.AQUA}가격: ${ChatColor.YELLOW}${it.get("price") as Int}캐시"))
                        lore.add(text("${ChatColor.LIGHT_PURPLE}판매자: ")
                            .append(
                                if(getInstance().server.onlinePlayers.any { a ->
                                    a.uniqueId.toString() == it.get("owner")
                                }){
                                    getInstance().server.onlinePlayers.first { a ->
                                        a.uniqueId.toString() == it.get("owner")
                                    }.displayName()
                                }
                                else {
                                    text("${ChatColor.GOLD}" + getInstance().server.offlinePlayers.first { a ->
                                        a.uniqueId.toString() == it.get("owner")
                                    }.name)
                                }
                            )
                        )

                        itemStack.lore(lore)

                        GuiItem(
                            itemStack
                        ){ e: InventoryClickEvent ->
                            e.isCancelled = true

                            val pl = getInstance().getPlayer(e.whoClicked as Player)
                            if(pl.cash < it.get("price") as Int){
                                e.whoClicked.sendMessage(userText("캐시가 모자랍니다."))
                            }
                            else {
                                getInstance().onlinePlayers[e.whoClicked.uniqueId.toString()]!!.cash -= it.get("price") as Int
                                if(e.whoClicked.uniqueId.toString() == it.get("owner") as String){
                                    e.whoClicked.sendMessage(
                                        userText("").append(itemStack.displayName()).append(Component.text(" 아이템을 회수했습니다."))
                                    )
                                }
                                else {
                                    e.whoClicked.sendMessage(
                                        userText("").append(itemStack.displayName()).append(Component.text(" 아이템을 ${it.get("price") as Int}캐시에 구입했습니다."))
                                    )
                                }

                                getInstance().onlinePlayers[it.get("owner") as String]!!.cash += it.get("price") as Int

                                e.whoClicked.inventory.addItem(
                                    (it.get("item") as ItemStack)
                                )
                                getInstance().userShopItem.remove(it)

                                e.inventory.close()
                                (e.whoClicked as Player).performCommand("ushop")
                            }
                        }
                    }
                )
                val navigation = StaticPane(1, 3, 7, 1)

                val rw = ItemStack(Material.RED_WOOL)
                var meta = rw.itemMeta
                meta.displayName(text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                rw.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        rw
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        if (paneItem.page > 0) {
                            paneItem.page = paneItem.page - 1
                            guiCashShop.update()
                        }
                    }, 0, 0
                )

                val gw = ItemStack(Material.GREEN_WOOL)
                meta = gw.itemMeta
                meta.displayName(Component.text("다음").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                gw.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        gw
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        if (paneItem.page < paneItem.pages - 1) {
                            paneItem.page = paneItem.page + 1
                            guiCashShop.update()
                        }
                    }, 6, 0
                )

                val br = ItemStack(Material.BARRIER)
                meta = br.itemMeta
                meta.displayName(Component.text("닫기").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
                br.itemMeta = meta

                navigation.addItem(
                    GuiItem(
                        br
                    ) { event: InventoryClickEvent ->
                        event.isCancelled = true

                        event.whoClicked.closeInventory()
                    }, 3, 0
                )

                guiCashShop.addPane(navigation)
                val background = OutlinePane(0, 0, 9, 4)
                val stack = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
                meta = stack.itemMeta
                meta.displayName(text(""))
                stack.itemMeta = meta

                background.addItem(GuiItem(stack))
                background.setRepeat(true)
                background.priority = Pane.Priority.LOWEST
                background.setOnClick { event: InventoryClickEvent ->
                    event.isCancelled = true
                }
                guiCashShop.addPane(paneItem)
                guiCashShop.addPane(background)

                guiCashShop.update()
                guiCashShop.show(player)
            }
        }

        register(getInstance(), "cshopadd"){
            requires { player.isOp }
            executes {
                player.sendMessage(adminText("물건 가격을 입력해주세요."))
            }
            then("price" to int(0, Integer.MAX_VALUE)){
                executes {
                    val price: Int by it
                    if(player.inventory.itemInMainHand.type != Material.AIR){
                        val item = player.inventory.itemInMainHand.clone()

                        val yml = YamlConfiguration()
                        yml.set("item", item)
                        yml.set("owner", "admin")
                        yml.set("price", price)

                        getInstance().cashShopItem.add(
                            yml
                        )
                        
                        player.sendMessage(adminText("아이템을 등록했습니다."))
                    }
                    else {
                        player.sendMessage(adminText("아이템을 들어주세요."))
                    }
                }
            }
        }

        register(getInstance(), "ushopadd", "판매", "sell"){
            executes {
                player.sendMessage(userText("물건 가격을 입력해주세요."))
            }
            then("price" to int(0, Integer.MAX_VALUE)){
                executes {
                    if(player.isOp){
                        player.sendMessage(adminText("캐시 상점 아이템은 캐시 상점에 등록해주세요."))
                    }

                    val price: Int by it
                    if(player.inventory.itemInMainHand.type != Material.AIR){
                        val item = player.inventory.itemInMainHand.clone()

                        val yml = YamlConfiguration()
                        yml.set("item", item)
                        yml.set("owner", player.uniqueId.toString())
                        yml.set("price", price)

                        getInstance().userShopItem.add(
                            yml
                        )

                        player.sendMessage(userText("아이템을 등록했습니다."))
                    }
                    else {
                        player.sendMessage(userText("아이템을 들어주세요."))
                    }
                }
            }
        }
    }
}