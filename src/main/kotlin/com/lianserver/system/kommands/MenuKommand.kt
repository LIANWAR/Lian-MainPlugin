package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.lianserver.system.interfaces.KommandInterface
import io.github.monun.kommand.Kommand.Companion.register
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class MenuKommand: KommandInterface {
    fun namedItemStack(m: Material, t: Component, l: List<Component> = listOf()): ItemStack {
        val st = ItemStack(m)
        val meta = st.itemMeta

        meta.displayName(t)
        meta.lore(l)

        st.itemMeta = meta

        return st
    }

    override fun kommand() {
        register(getInstance(), "menu"){
            executes {
                val gui = ChestGui(1, "리안서버 메뉴")

                val spPanel = StaticPane(0, 0, 9, 1)
                spPanel.addItem(
                    GuiItem(
                        namedItemStack(Material.BEACON, text("스폰으로").color(NamedTextColor.GREEN))
                    ) { e: InventoryClickEvent ->
                        e.whoClicked.teleport(Location(getInstance().server.getWorld("spawn"), 0.5, 57.0, 0.5))
                    },
                    1,
                    0
                )

                spPanel.addItem(
                    GuiItem(
                        namedItemStack(Material.GRASS_BLOCK, text("야생으로").color(NamedTextColor.GREEN))
                    ) { e: InventoryClickEvent ->
                        val xzPos = Pair(Random.nextInt(-10000, 10001), Random.nextInt(-10000, 10001))
                        val yPos = getInstance().server.getWorld("world")!!.getHighestBlockYAt(xzPos.first, xzPos.second)

                        e.whoClicked.teleport(Location(getInstance().server.getWorld("world"), xzPos.first.toDouble(), yPos + 1.0, xzPos.second.toDouble()))
                    },
                    4,
                    0
                )

                spPanel.addItem(
                    GuiItem(
                        namedItemStack(Material.GRASS_BLOCK, text("야생으로").color(NamedTextColor.GREEN))
                    ) { e: InventoryClickEvent ->
                        if(getInstance().getPlayer(e.whoClicked as Player).clan == null && getInstance().getPlayer(e.whoClicked as Player).country == null){
                            val guiNoClan = ChestGui(1, "클랜 메뉴 - 클랜 없음")
                            val pane = StaticPane(0, 0, 9, 1)

                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.WRITABLE_BOOK, text("클랜 생성").color(NamedTextColor.GREEN))
                                ) { e: InventoryClickEvent ->
                                    val guiClanName = AnvilGui("클랜의 이름을 쓰고 창을 닫아주세요")
                                    guiClanName.setOnClose {
                                        if(guiClanName.renameText != ""){
                                            (it.player as Player).performCommand("/clan create ${guiClanName.renameText}")
                                        }
                                        else {
                                            it.player.sendMessage(clanText("클랜 생성이 취소되었습니다."))
                                        }
                                    }

                                    guiClanName.update()
                                    guiClanName.show(e.whoClicked)
                                },
                                1,
                                0
                            )
                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.PAPER, text("대기 중인 초대 수락").color(NamedTextColor.YELLOW))
                                ) { e: InventoryClickEvent ->
                                    (e.whoClicked as Player).performCommand("/clan accept")
                                    (e.whoClicked as Player).performCommand("/country accept")
                                },
                                3,
                                0
                            )
                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.SPYGLASS, text("공개된 클랜 보기").color(NamedTextColor.AQUA))
                                ) { e: InventoryClickEvent ->
                                    (e.whoClicked as Player).performCommand("/clan public")
                                },
                                5,
                                0
                            )
                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.SPYGLASS, text("공개된 국가 보기").color(NamedTextColor.AQUA))
                                ) { e: InventoryClickEvent ->
                                    (e.whoClicked as Player).performCommand("/country public")
                                },
                                7,
                                0
                            )

                            guiNoClan.addPane(pane)
                            guiNoClan.update()

                            guiNoClan.show(e.whoClicked)
                        }
                        else {
                            if(getInstance().getPlayer(e.whoClicked as Player).clan != null){
                                val p = getInstance().getPlayer(e.whoClicked as Player)

                                if(p.clan!!.owner.player.uniqueId == e.whoClicked.uniqueId){
                                    val guiClanMenuAdv = ChestGui(1, "클랜 메뉴 - ${p.clan!!.name} (관리)")
                                }
                            }
                        }
                    },
                    7,
                    0
                )

                gui.addPane(spPanel)
                gui.update()

                gui.show(player)
            }
        }
    }

    init {}
}