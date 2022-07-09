package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.lianserver.system.interfaces.KommandInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.kommand.Kommand.Companion.register
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Levelled
import org.bukkit.block.data.type.Light
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockDataMeta
import org.bukkit.inventory.meta.BlockStateMeta
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class MiscKommand: KommandInterface, PrefixedTextInterface {
    fun namedItemStack(m: Material, t: Component, l: List<Component> = listOf()): ItemStack {
        val st = ItemStack(m)
        val meta = st.itemMeta

        meta.displayName(t.decoration(TextDecoration.ITALIC, false))
        meta.lore(l.map { it.decoration(TextDecoration.ITALIC, false) })

        ItemFlag.values().forEach { meta.addItemFlags(it) }

        st.itemMeta = meta

        return st
    }

    override fun kommand() {
        register(getInstance(), "lianhelp", "명령어", "도움말"){
            then("shop"){
                executes {
                    sender.sendMessage(userText("리안서버 버전 ${getInstance().description.version} 상점 도움말"))
                    sender.sendMessage(userText(" * /cshop: 후원으로 구매할 수 있는 아이템을 보고 구매합니다."))
                    sender.sendMessage(userText(" * /ushop: 유저들이 등록한 아이템을 보고 구매합니다."))
                    sender.sendMessage(userText(" * /sell <가격>: 들고 있는 아이템을 상점에 등록합니다."))
                }
            }
            then("etc"){
                executes {
                    sender.sendMessage(userText("리안서버 버전 ${getInstance().description.version} 기타 도움말"))
                    sender.sendMessage(userText(" * /help: 도움말을 보여줍니다."))
                    sender.sendMessage(userText(" * /license: 플러그인 및 리소스팩의 라이센스를 볼 수 있습니다."))
                    sender.sendMessage(userText(" * /lotto: 철괴 8개로 다양한 아이템들을 얻을 수 있습니다."))
                    sender.sendMessage(userText(" * /출석체크: 매일매일 출석 체크를 할 수 있습니다. 연속으로 출석 체크를 한 일수마다 보상이 달라집니다."))
                }
            }
            executes {
                sender.sendMessage(userText("리안서버 버전 ${getInstance().description.version} 도움말"))
                sender.sendMessage(userText(" * 클랜 도움말: /clan help"))
                sender.sendMessage(userText(" * 국가 도움말: /country help"))
                sender.sendMessage(userText(" * 상점 도움말: /help shop"))
                sender.sendMessage(userText(" * 기타 도움말: /help etc"))
            }
        }
        register(getInstance(), "license") {
            executes {
                sender.sendMessage(adminText("Lian-Server/Lian-MainPlugin ${getInstance().description.version} - GNU 일반 공중 사용 허가서 3.0 (GPLv3)"))
                sender.sendMessage(adminText("의존 라이브러리: "))
                sender.sendMessage(adminText(" * monun/kommand: 2.12.0"))
                sender.sendMessage(adminText(" * org.reflections:reflections: 0.10.2 (GitHub에 없음)"))
                sender.sendMessage(adminText(" * stefvanschie/IF: 0.10.6"))
                sender.sendMessage(adminText(" * WesJD/AnvilGUI: 수동 빌드"))
                sender.sendMessage(adminText("Lian-Server/effect-in-item, skin-scroll 1.1 - GNU 일반 공중 사용 허가서 3.0 (GPLv3)"))
                sender.sendMessage(adminText("의존 라이브러리: "))
                sender.sendMessage(adminText(" * monun/kommand: 2.12.0"))
                sender.sendMessage(adminText(" * org.reflections:reflections: 0.10.2 (GitHub에 없음)"))
                sender.sendMessage(adminText(" * stefvanschie/IF: 0.10.6"))
                sender.sendMessage(adminText("리소스팩 - AlphaGot 제작"))
                sender.sendMessage(adminText("폰트 라이센스: "))
                sender.sendMessage(adminText(" * IBM Plex Sans - SIL 오픈 폰트 라이선스 (v1.1)"))
            }
        }
        register(getInstance(), "lotto"){
            executes {
                var amount = 0
                for (item in player.inventory.contents?.filterNotNull() ?: listOf()) {
                    if (item.type == Material.IRON_INGOT) {
                        amount += item.amount
                    }
                }
                if(amount >= 8){
                    for(i in 0..7){
                        player.inventory.removeItem(ItemStack(Material.IRON_INGOT))
                    }

                    val stack = when(Random.nextInt(0, 1000)){
                        in 0..9 -> ItemStack(Material.NETHERITE_INGOT)
                        in 10..39 -> ItemStack(Material.DIAMOND, 8)
                        in 40..399 -> ItemStack(Material.DIAMOND)
                        else -> ItemStack(Material.AIR)
                    }

                    player.inventory.addItem(stack)

                    if(stack.type == Material.AIR){
                        player.sendMessage(userText("꽝!"))
                    }
                    else {
                        player.sendMessage(
                            when(stack){
                                ItemStack(Material.NETHERITE_INGOT) -> userText("1등 당첨!")
                                ItemStack(Material.DIAMOND, 8) -> userText("2등 당첨!")
                                ItemStack(Material.DIAMOND, 1) -> userText("3등 당첨!")
                                else -> userText("버그 당첨!")
                            }
                        )
                    }
                }
                else {
                    player.sendMessage(userText("철괴가 모자랍니다."))
                }
            }
        }
        register(getInstance(), "출첵", "출석체크", "출석"){
            executes {
                if(getInstance().getPlayer(player).lastCCDay != SimpleDateFormat("yyyyMMdd").format(Date())){
                    getInstance().onlinePlayers[player.uniqueId.toString()]!!.lastCCDay = SimpleDateFormat("yyyyMMdd").format(Date())
                    player.sendMessage(userText("오늘도 리안서버를 플레이해주셔서 감사합니다!"))

                    val ccs = getInstance().onlinePlayers[player.uniqueId.toString()]!!.ccStreak

                    when(ccs){
                        0 -> {
                            player.inventory.addItem(namedItemStack(Material.PAPER, text("로또").color(NamedTextColor.AQUA)))
                        }
                        in 1..3 -> {
                            player.inventory.addItem(ItemStack(Material.DIAMOND, ccs))
                        }
                        4 -> {
                            player.inventory.addItem(ItemStack(Material.NETHERITE_SCRAP, 1))
                        }
                        in 5..6 -> {
                            getInstance().onlinePlayers[player.uniqueId.toString()]!!.cash += 20 * (ccs - 4)
                        }
                    }
                    if(ccs == 6){
                        getInstance().onlinePlayers[player.uniqueId.toString()]!!.ccStreak = 0
                    }
                    else {
                        getInstance().onlinePlayers[player.uniqueId.toString()]!!.ccStreak++
                    }

                    val guiCCPane: ChestGui = ChestGui.load(getInstance(), getInstance().getResource("gui/gui_cc_pane.xml")!!)!!
                    val guiCCSP: StaticPane = guiCCPane.panes.first { it is StaticPane } as StaticPane

                    intArrayOf(1, 2, 3, 4, 5, 6, 7).map {
                        val item = namedItemStack(Material.LIGHT, text("${it}일"))

                        val bDR = item.itemMeta as BlockDataMeta
                        val bD = item.type.createBlockData() as Levelled
                        bD.level = it
                        bDR.setBlockData(bD)
                        item.itemMeta = bDR

                        guiCCSP.addItem(
                            GuiItem(
                                item
                            ){
                                it.isCancelled = true
                            },
                            it - 1,
                            0
                        )
                    }
                    intArrayOf(0, 1, 2, 3, 4, 5, 6).map {
                        val item = namedItemStack(
                            if(ccs < it) Material.RED_STAINED_GLASS_PANE
                            else Material.LIME_STAINED_GLASS_PANE,
                            text(
                                if(ccs < it) "${ChatColor.RED}출석체크 안 함"
                                else "${ChatColor.GREEN}출석체크 완료"
                            )
                        )

                        guiCCSP.addItem(
                            GuiItem(
                                item
                            ){
                                it.isCancelled = true
                            },
                            it,
                            1
                        )
                    }
                    intArrayOf(0, 1, 2, 3, 4, 5, 6).map {
                        val item = when(it){
                            0 -> {
                                namedItemStack(Material.PAPER, text("로또").color(NamedTextColor.AQUA))
                            }
                            in 1..3 -> {
                                ItemStack(Material.DIAMOND, it)
                            }
                            4 -> {
                                ItemStack(Material.NETHERITE_SCRAP, 1)
                            }
                            in 5..6 -> {
                                namedItemStack(Material.GOLD_NUGGET, text("${20 * (it - 4)}캐시").color(NamedTextColor.LIGHT_PURPLE))
                            }
                            else -> {
                                ItemStack(Material.AIR)
                            }
                        }

                        guiCCSP.addItem(
                            GuiItem(
                                item
                            ){
                                it.isCancelled = true
                            },
                            it,
                            2
                        )
                    }

                    guiCCPane.update()
                    guiCCPane.setOnGlobalClick {
                        it.isCancelled = true
                    }

                    guiCCPane.show(player)
                }
                else {
                    player.sendMessage(userText("이미 출석체크를 했습니다."))
                }
            }
        }
    }

    init {}
}