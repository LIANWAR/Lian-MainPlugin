package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.google.gson.GsonBuilder
import com.lianserver.system.common.ShopItem
import com.lianserver.system.interfaces.KommandInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.kommand.Kommand.Companion.register
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.io.File

class ShopKommand: KommandInterface, PrefixedTextInterface {
    private lateinit var cashShopItem: List<ShopItem>
    private lateinit var userShopItem: List<ShopItem>

    private fun namedItemStack(m: Material, t: Component, l: List<Component> = listOf()): ItemStack {
        val st = ItemStack(m)
        val meta = st.itemMeta

        meta.displayName(t.decoration(TextDecoration.ITALIC, false))
        meta.lore(l.map { it.decoration(TextDecoration.ITALIC, false) })

        ItemFlag.values().forEach { meta.addItemFlags(it) }

        st.itemMeta = meta

        return st
    }

    override fun kommand() {
        register(getInstance(), "cshop", "캐시상점"){
            executes {
                val guiCashShop = ChestGui(6, "캐시 상점")
                guiCashShop.setOnGlobalClick { e: InventoryClickEvent ->
                    e.isCancelled = true
                }
                val paneItem = PaginatedPane(0, 0, 9, 5)
                paneItem.populateWithGuiItems(
                    cashShopItem.map {
                        GuiItem(
                            namedItemStack(
                                Material.valueOf(it.item),
                                it.name,
                                it.lore.toList()
                            )
                        ){ e: InventoryClickEvent ->
                            e.isCancelled = true

                            val pl = getInstance().getPlayer(e.whoClicked as Player)
                            if(pl.cash < it.price){
                                e.whoClicked.sendMessage(userText("캐시가 모자랍니다."))
                            }
                            else {
                                getInstance().onlinePlayers[e.whoClicked.uniqueId.toString()]!!.cash -= it.price
                                e.whoClicked.sendMessage(userText("").append(it.name).append(Component.text(" 아이템을 ${it.price}캐시에 구입했습니다.")))
                                e.currentItem!!.amount -= 1

                                val item = namedItemStack(
                                    Material.valueOf(it.item),
                                    it.name,
                                    it.lore.toList()
                                )
                                item.itemMeta = it.meta

                                e.whoClicked.inventory.addItem(
                                    item
                                )
                            }
                        }
                    }
                )
                val navigation = StaticPane(0, 5, 9, 1)

                val rw = ItemStack(Material.RED_WOOL)
                var meta = rw.itemMeta
                meta.displayName(Component.text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
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
                    }, 8, 0
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
                    }, 4, 0
                )

                guiCashShop.addPane(navigation)
                val background = OutlinePane(0, 5, 9, 1)
                background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                background.setRepeat(true)
                background.priority = Pane.Priority.LOWEST
                background.setOnClick { event: InventoryClickEvent ->
                    event.isCancelled = true
                }
                guiCashShop.addPane(paneItem)
                guiCashShop.addPane(background)
            }
        }
        register(getInstance(), "ushop", "유저상점"){
            executes {
                val guiCashShop = ChestGui(6, "유저 상점")
                guiCashShop.setOnGlobalClick { e: InventoryClickEvent ->
                    e.isCancelled = true
                }
                val paneItem = PaginatedPane(0, 0, 9, 5)
                paneItem.populateWithGuiItems(
                    cashShopItem.map {
                        GuiItem(
                            namedItemStack(
                                Material.valueOf(it.item),
                                it.name,
                                it.lore.toList()
                            )
                        ){ e: InventoryClickEvent ->
                            e.isCancelled = true

                            val pl = getInstance().getPlayer(e.whoClicked as Player)
                            if(pl.cash < it.price){
                                e.whoClicked.sendMessage(userText("캐시가 모자랍니다."))
                            }
                            else {
                                getInstance().onlinePlayers[e.whoClicked.uniqueId.toString()]!!.cash -= it.price
                                e.whoClicked.sendMessage(userText("").append(it.name).append(Component.text(" 아이템을 ${it.price}캐시에 구입했습니다.")))
                                e.currentItem!!.amount -= 1

                                val item = namedItemStack(
                                    Material.valueOf(it.item),
                                    it.name,
                                    it.lore.toList()
                                )
                                item.itemMeta = it.meta

                                e.whoClicked.inventory.addItem(
                                    item
                                )
                            }
                        }
                    }
                )
                val navigation = StaticPane(0, 5, 9, 1)

                val rw = ItemStack(Material.RED_WOOL)
                var meta = rw.itemMeta
                meta.displayName(Component.text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
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
                    }, 8, 0
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
                    }, 4, 0
                )

                guiCashShop.addPane(navigation)
                val background = OutlinePane(0, 5, 9, 1)
                background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                background.setRepeat(true)
                background.priority = Pane.Priority.LOWEST
                background.setOnClick { event: InventoryClickEvent ->
                    event.isCancelled = true
                }
                guiCashShop.addPane(paneItem)
                guiCashShop.addPane(background)
            }
        }
    }

    init {
        val gb = GsonBuilder()
            .setPrettyPrinting()
            .create()
        cashShopItem = gb.fromJson(File("plugins/LianMain/shop/cshop.json").readText(), arrayOf<ShopItem>().javaClass).toList()
        userShopItem = gb.fromJson(File("plugins/LianMain/shop/ushop.json").readText(), arrayOf<ShopItem>().javaClass).toList()
    }
}