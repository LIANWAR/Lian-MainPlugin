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
import net.kyori.adventure.text.format.TextDecoration
import net.wesjd.anvilgui.AnvilGUI
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

        meta.displayName(t.decoration(TextDecoration.ITALIC, false))
        meta.lore(l.map { it.decoration(TextDecoration.ITALIC, false) })

        st.itemMeta = meta

        return st
    }

    override fun kommand() {
        register(getInstance(), "메뉴", "apsb", "menu", "gui", "매뉴", "aosb"){
            executes {
                val gui = ChestGui(1, "리안서버 메뉴")

                val spPanel = StaticPane(0, 0, 9, 1)
                spPanel.addItem(
                    GuiItem(
                        namedItemStack(Material.BEACON, text("스폰으로").color(NamedTextColor.GREEN))
                    ) { e: InventoryClickEvent ->
                        e.isCancelled = true
                        e.whoClicked.teleport(Location(getInstance().server.getWorld("spawn"), 0.5, 57.0, 0.5))
                    },
                    1,
                    0
                )

                spPanel.addItem(
                    GuiItem(
                        namedItemStack(Material.GRASS_BLOCK, text("야생으로").color(NamedTextColor.GREEN))
                    ) { e: InventoryClickEvent ->
                        e.isCancelled = true

                        val xzPos = Pair(Random.nextInt(-10000, 10001), Random.nextInt(-10000, 10001))
                        val yPos = getInstance().server.getWorld("world")!!.getHighestBlockYAt(xzPos.first, xzPos.second)

                        e.whoClicked.teleport(Location(getInstance().server.getWorld("world"), xzPos.first.toDouble(), yPos + 1.0, xzPos.second.toDouble()))
                    },
                    4,
                    0
                )

                spPanel.addItem(
                    GuiItem(
                        namedItemStack(Material.STONE_SWORD, text("클랜/국가 메뉴").color(NamedTextColor.GREEN))
                    ) { e: InventoryClickEvent ->
                        e.isCancelled = true

                        if(getInstance().getPlayer(e.whoClicked as Player).clan == null && getInstance().getPlayer(e.whoClicked as Player).country == null){
                            val guiNoClan = ChestGui(1, "클랜 메뉴 - 클랜 없음")
                            val pane = StaticPane(0, 0, 9, 1)

                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.WRITABLE_BOOK, text("클랜 생성").color(NamedTextColor.GREEN))
                                ) { e: InventoryClickEvent ->
                                    e.isCancelled = true

                                    AnvilGUI.Builder()
                                        .onComplete { player: Player, text: String ->
                                            player.performCommand("clan create ${text}")
                                            return@onComplete AnvilGUI.Response.close()
                                        }
                                        .text("Running in the 20s") //sets the text the GUI should start with
                                        .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
                                        .title("클랜의 이름을 써주세요") //set the title of the GUI (only works in 1.14+)
                                        .plugin(getInstance()) //set the plugin instance
                                        .open(e.whoClicked as Player)
                                },
                                1,
                                0
                            )
                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.PAPER, text("대기 중인 초대 수락").color(NamedTextColor.YELLOW))
                                ) { e: InventoryClickEvent ->
                                    e.isCancelled = true

                                    (e.whoClicked as Player).performCommand("clan accept")
                                    (e.whoClicked as Player).performCommand("country accept")
                                },
                                3,
                                0
                            )
                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.SPYGLASS, text("공개된 클랜 보기").color(NamedTextColor.AQUA))
                                ) { e: InventoryClickEvent ->
                                    e.isCancelled = true

                                    (e.whoClicked as Player).performCommand("clan public")
                                },
                                5,
                                0
                            )
                            pane.addItem(
                                GuiItem(
                                    namedItemStack(Material.SPYGLASS, text("공개된 국가 보기").color(NamedTextColor.AQUA))
                                ) { e: InventoryClickEvent ->
                                    e.isCancelled = true

                                    (e.whoClicked as Player).performCommand("country public")
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
                                    val pCMA = StaticPane(0, 0, 9, 1)
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.BOOK,
                                                text("클랜 정보 보기").color(NamedTextColor.GOLD)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan info")
                                        },
                                        0, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.SPYGLASS,
                                                text("모든 클랜 보기").color(NamedTextColor.LIGHT_PURPLE)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan all")
                                        },
                                        1, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.PAPER,
                                                text("클랜 채팅/전체 채팅 전환").color(NamedTextColor.AQUA)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan chat")
                                        },
                                        2, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.TNT,
                                                text("클랜 삭제").color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true),
                                                listOf(
                                                    text("이 작업은 절대로 되돌릴 수 없습니다!").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.UNDERLINED, true)
                                                )
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan leave delete")
                                        },
                                        3, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.IRON_SWORD,
                                                text("클랜원 강제 퇴출").color(NamedTextColor.RED)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            AnvilGUI.Builder()
                                                .onComplete { player: Player, text: String ->
                                                    player.performCommand("clan kick $text")
                                                    return@onComplete AnvilGUI.Response.close()
                                                }
                                                .text("Running in the 20s") //sets the text the GUI should start with
                                                .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
                                                .title("강퇴할 클랜원의 이름을 써주세요") //set the title of the GUI (only works in 1.14+)
                                                .plugin(getInstance()) //set the plugin instance
                                                .open(e.whoClicked as Player)
                                        },
                                        4, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.FISHING_ROD,
                                                text("클랜에 플레이어 초대").color(NamedTextColor.GREEN)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            AnvilGUI.Builder()
                                                .onComplete { player: Player, text: String ->
                                                    player.performCommand("clan invite ${text}")
                                                    return@onComplete AnvilGUI.Response.close()
                                                }
                                                .text("Running in the 20s") //sets the text the GUI should start with
                                                .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
                                                .title("초대할 사람의 이름을 써주세요") //set the title of the GUI (only works in 1.14+)
                                                .plugin(getInstance()) //set the plugin instance
                                                .open(e.whoClicked as Player)
                                        },
                                        5, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.MAP,
                                                text("클랜 공개/비공개 상태 전환").color(NamedTextColor.LIGHT_PURPLE)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan togglepublic")
                                        },
                                        6, 0
                                    )

                                    guiClanMenuAdv.addPane(pCMA)
                                    guiClanMenuAdv.update()

                                    guiClanMenuAdv.show(e.whoClicked)
                                }
                                else {
                                    val guiClanMenu = ChestGui(1, "클랜 메뉴 - ${p.clan!!.name}")
                                    val pCMA = StaticPane(0, 0, 9, 1)
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.BOOK,
                                                text("클랜 정보 보기").color(NamedTextColor.GOLD)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan info")
                                        },
                                        0, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.SPYGLASS,
                                                text("모든 클랜 보기").color(NamedTextColor.LIGHT_PURPLE)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan all")
                                        },
                                        1, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.PAPER,
                                                text("클랜 채팅/전체 채팅 전환").color(NamedTextColor.AQUA)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan chat")
                                        },
                                        2, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.TNT,
                                                text("클랜 나가기").color(NamedTextColor.RED),
                                                listOf(
                                                    text("나간 다음 다시 클랜에 들어오려면 초대를 받거나").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                                                    text("공개 클랜 창에서 들어올 수 있습니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                                )
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("clan leave")
                                        },
                                        3, 0
                                    )

                                    guiClanMenu.addPane(pCMA)
                                    guiClanMenu.update()

                                    guiClanMenu.show(e.whoClicked)
                                }
                            }
                            else {
                                val p = getInstance().getPlayer(e.whoClicked as Player)

                                if(p.country!!.owner.player.uniqueId == e.whoClicked.uniqueId){
                                    val guiClanMenuAdv = ChestGui(1, "국가 메뉴 - ${p.country!!.name} (관리)")
                                    val pCMA = StaticPane(0, 0, 9, 1)
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.BOOK,
                                                text("국가 정보 보기").color(NamedTextColor.GOLD)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country info")
                                        },
                                        0, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.SPYGLASS,
                                                text("모든 국가 보기").color(NamedTextColor.LIGHT_PURPLE)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country all")
                                        },
                                        1, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.PAPER,
                                                text("국가 채팅/전체 채팅 전환").color(NamedTextColor.AQUA)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country chat")
                                        },
                                        2, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.TNT,
                                                text("국가 삭제").color(NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, true),
                                                listOf(
                                                    text("이 작업은 절대로 되돌릴 수 없습니다!").color(NamedTextColor.RED).decoration(TextDecoration.BOLD, true).decoration(TextDecoration.UNDERLINED, true)
                                                )
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country leave delete")
                                        },
                                        3, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.IRON_SWORD,
                                                text("국민 강제 퇴출").color(NamedTextColor.RED)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            AnvilGUI.Builder()
                                                .onComplete { player: Player, text: String ->
                                                    player.performCommand("country kick $text")
                                                    return@onComplete AnvilGUI.Response.close()
                                                }
                                                .text("Running in the 20s") //sets the text the GUI should start with
                                                .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
                                                .title("강퇴할 국민의 이름을 써주세요") //set the title of the GUI (only works in 1.14+)
                                                .plugin(getInstance()) //set the plugin instance
                                                .open(e.whoClicked as Player)
                                        },
                                        4, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.FISHING_ROD,
                                                text("국가에 플레이어 초대").color(NamedTextColor.GREEN)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            AnvilGUI.Builder()
                                                .onComplete { player: Player, text: String ->
                                                    player.performCommand("country invite $text")
                                                    return@onComplete AnvilGUI.Response.close()
                                                }
                                                .text("Running in the 20s") //sets the text the GUI should start with
                                                .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
                                                .title("초대할 사람의 이름을 써주세요") //set the title of the GUI (only works in 1.14+)
                                                .plugin(getInstance()) //set the plugin instance
                                                .open(e.whoClicked as Player)
                                        },
                                        5, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.MAP,
                                                text("국가 공개/비공개 상태 전환").color(NamedTextColor.LIGHT_PURPLE)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country togglepublic")
                                        },
                                        6, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.FISHING_ROD,
                                                text("다른 국가에 선전포고").color(NamedTextColor.RED)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            AnvilGUI.Builder()
                                                .onComplete { player: Player, text: String ->
                                                    player.performCommand("country wardecl ${text}")
                                                    return@onComplete AnvilGUI.Response.close()
                                                }
                                                .text("Running in the 20s") //sets the text the GUI should start with
                                                .itemLeft(ItemStack(Material.PAPER)) //use a custom item for the first slot
                                                .title("선전포고할 국가의 이름을 써주세요") //set the title of the GUI (only works in 1.14+)
                                                .plugin(getInstance()) //set the plugin instance
                                                .open(e.whoClicked as Player)
                                        },
                                        7, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.MAP,
                                                text("다른 국가에서 온 선전포고에 응하기").color(NamedTextColor.RED)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country waraccept")
                                        },
                                        8, 0
                                    )
                                    
                                    guiClanMenuAdv.addPane(pCMA)
                                    guiClanMenuAdv.update()

                                    guiClanMenuAdv.show(e.whoClicked)
                                }
                                else {
                                    val guiClanMenu = ChestGui(1, "국가 메뉴 - ${p.country!!.name}")
                                    val pCMA = StaticPane(0, 0, 9, 1)
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.BOOK,
                                                text("국가 정보 보기").color(NamedTextColor.GOLD)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country info")
                                        },
                                        0, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.SPYGLASS,
                                                text("모든 국가 보기").color(NamedTextColor.LIGHT_PURPLE)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country all")
                                        },
                                        1, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.PAPER,
                                                text("국가 채팅/전체 채팅 전환").color(NamedTextColor.AQUA)
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country chat")
                                        },
                                        2, 0
                                    )
                                    pCMA.addItem(
                                        GuiItem(
                                            namedItemStack(
                                                Material.TNT,
                                                text("국가 나가기").color(NamedTextColor.RED),
                                                listOf(
                                                    text("나간 다음 다시 국가에 들어오려면 초대를 받거나").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                                                    text("공개 국가 창에서 들어올 수 있습니다.").color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                                                )
                                            )
                                        ) {
                                            it.isCancelled = true

                                            (it.whoClicked as Player).performCommand("country leave")
                                        },
                                        3, 0
                                    )

                                    guiClanMenu.addPane(pCMA)
                                    guiClanMenu.update()

                                    guiClanMenu.show(e.whoClicked)
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