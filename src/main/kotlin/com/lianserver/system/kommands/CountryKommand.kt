package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.lianserver.system.common.Country
import com.lianserver.system.common.LianPlayer
import com.lianserver.system.common.War
import com.lianserver.system.interfaces.KommandInterface
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

/***
 * @author AlphaGot
 */

class CountryKommand: KommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("country") {
                then("help"){
                    executes {
                        sender.sendMessage(
                                    "${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}- 국가 도움말 ${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-${ChatColor.WHITE}-${ChatColor.AQUA}-\n" +
                                    "${ChatColor.RESET}${countryTextS("${ChatColor.WHITE} * /country info: 국가의 정보를 보여줍니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country invite <플레이어>: <플레이어>를 국가에 초대합니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country accept: 대기 중인 국가 가입 요청을 수락합니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country all: 모든 국가를 보여줍니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country chat: 국가 채팅 모드를 전환합니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country leave: 국가를 나갑니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country kick <플레이어>: <플레이어>를 추방합니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country wardecl <국가 이름>: <국가 이름> 국가에 선전포고 입장을 보냅니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country waraccept: 대기 중인 선전포고에 응합니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country public: 공개된 국가 목록을 보여줍니다.")}\n" +
                                    "${countryTextS("${ChatColor.WHITE} * /country togglepublic: 국가의 공개 상태를 전환합니다.")}\n"
                        )
                    }
                }
                executes { sender.sendMessage(countryText("/country help")) }
                then("info") {
                    then("clanName" to string(StringType.GREEDY_PHRASE)){
                        executes {
                            val clanName: String by it

                            if(getInstance().countries.any { it.value.name == clanName }){
                                val clan = getInstance().countries.values.first { it.name == clanName }

                                sender.sendMessage(countryText("국가 정보"))
                                sender.sendMessage("${ChatColor.WHITE}<${ChatColor.YELLOW}${clan.name} 국가${ChatColor.WHITE}>")
                                clan.players.forEach {
                                    sender.sendMessage(" - ${ChatColor.YELLOW}${it.player.name}")
                                }
                                sender.sendMessage(countryText("승전 수: ${clan.winCount}회"))
                            }
                            else {
                                sender.sendMessage(countryText("해당 이름을 가진 국가가 없습니다."))
                            }
                        }
                    }
                    executes {
                        if(getInstance().getPlayer(sender).country != null){
                            sender.sendMessage(countryText("국가 정보"))
                            sender.sendMessage("${ChatColor.WHITE}<${ChatColor.YELLOW}${getInstance().getPlayer(sender).country!!.name} 국가${ChatColor.WHITE}>")
                            getInstance().getPlayer(sender).country!!.players.forEach {
                                sender.sendMessage(" - ${ChatColor.YELLOW}${it.player.name}")
                            }
                            if(getInstance().getPlayer(sender).country!!.land != null){
                                sender.sendMessage(countryText("땅 좌표: ${getInstance().getPlayer(sender).country!!.land}"))
                            }
                            sender.sendMessage(countryText("승전 수: ${getInstance().getPlayer(sender).country!!.winCount}회"))
                        }
                        else {
                            sender.sendMessage(countryText("국가에 소속되어있지 않습니다!"))
                        }
                    }
                }
                then("invite"){
                    executes {
                        sender.sendMessage(countryText("누구를 초대할 지를 입력해주세요."))
                    }
                    then("target" to player()){
                        executes {
                            if (getInstance().getPlayer(sender).country == null) {
                                sender.sendMessage(countryText("국가에 소속되어있지 않습니다!"))
                            }
                            else {
                                if (getInstance().getPlayer(sender).country!!.owner != getInstance().getPlayer(sender)) {
                                    sender.sendMessage(countryText("수령만 초대할 수 있습니다."))
                                } else {
                                    if(getInstance().getPlayer(sender).country!!.players.size < 8){
                                        val target: Player by it

                                        if(getInstance().onlinePlayers.containsKey(target.uniqueId.toString())){
                                            getInstance().invitesCountry[target.uniqueId.toString()] = getInstance().getPlayer(sender).country!!
                                            sender.sendMessage(countryText("초대장을 보냈습니다."))
                                            if(getInstance().getPlayer(target).player.isOnline){
                                                val p = getInstance().server.onlinePlayers.first { it.uniqueId == getInstance().getPlayer(target).player.uniqueId }
                                                p.sendMessage(countryText("${sender.name}님이 국가 초대장을 보냈습니다. (${getInstance().getPlayer(sender).country!!.name} 국가)"))
                                                val clickComp = net.md_5.bungee.api.chat.TextComponent("[ 수락 ]")
                                                clickComp.color = ChatColor.GREEN
                                                clickComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("국가 수락하기").create())
                                                clickComp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/country accept")
                                                p.sendMessage(clickComp)

                                                getInstance().invitesCountryTaskId[target.uniqueId.toString()] = getInstance().server.scheduler.scheduleSyncDelayedTask(getInstance(),
                                                    {
                                                        getInstance().invitesCountry.remove(target.uniqueId.toString())
                                                        target.sendMessage(countryText("${sender.name}님에게서 온 국가 초대가 만료되었습니다."))
                                                    },
                                                    600L
                                                )
                                            }
                                        }
                                        else {
                                            sender.sendMessage(countryText("해당하는 이름의 플레이어가 없습니다."))
                                        }
                                    }
                                    else {
                                        player.sendMessage(countryText("국가 인원은 수령 포함 8명입니다."))
                                    }
                                }
                            }
                        }
                    }
                }
                then("accept"){
                    executes {
                        val p = getInstance().getPlayer(player)
                        if(getInstance().invitesCountry.containsKey(p.player.uniqueId.toString())){
                            if(getInstance().invitesCountry[p.player.uniqueId.toString()]!!.players.size < 8){
                                if(p.country == null && p.clan == null){
                                    p.country = getInstance().invitesCountry[p.player.uniqueId.toString()]
                                    getInstance().onlinePlayers[p.player.uniqueId.toString()] = p
                                    getInstance().countries[getInstance().invitesCountry[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.players =
                                        getInstance().countries[getInstance().invitesCountry[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.players.plusElement(p) as MutableList<LianPlayer>
                                    getInstance().countries[getInstance().invitesCountry[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.players.forEach { pl ->
                                        if(pl.player.isOnline){
                                            val p2 = getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }
                                            p2.sendMessage("${p.player.name}님이 국가에 가입했습니다. 환영 인사 한 번씩 해주세요!")
                                        }
                                    }
                                    player.sendMessage("${getInstance().countries[getInstance().invitesCountry[p.player.uniqueId.toString()]!!.owner.player.uniqueId.toString()]!!.name} 국가에 가입했습니다.")
                                    getInstance().invitesCountry.remove(player.uniqueId.toString())
                                    getInstance().server.scheduler.cancelTask(getInstance().invitesCountryTaskId[player.uniqueId.toString()]!!)
                                    getInstance().invitesCountryTaskId.remove(player.uniqueId.toString())
                                }
                                else {
                                    sender.sendMessage(countryText("이미 다른 클랜/국가에 속해있습니다."))
                                }
                            }
                            else {
                                player.sendMessage(countryText("국가 인원은 수령 포함 8명입니다."))
                            }
                        }
                        else {
                            player.sendMessage(countryText("대기 중인 초대가 없습니다."))
                        }
                    }
                }
                then("leave"){
                    executes {
                        if(getInstance().getPlayer(player).country == null){
                            player.sendMessage(countryText("나갈 국가가 없습니다."))
                        }
                        else {
                            if(getInstance().getPlayer(player).country!!.owner.player.uniqueId == player.uniqueId){
                                player.sendMessage(countryText("현재 국가의 수령입니다. 진짜로 국가를 삭제하시려면 /country leave deleteclan 명령어를 입력해주세요."))
                            }
                            else {
                                getInstance().getPlayer(player).country!!.players.forEach {pl ->
                                    if(pl.player.isOnline){
                                        (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(countryText("${player.name}님이 국가에서 나가셨습니다."))
                                    }
                                }
                                getInstance().countries[getInstance().getPlayer(player).country!!.owner.player.uniqueId.toString()]!!.players.remove(getInstance().getPlayer(player))
                                player.sendMessage(countryText("국가에서 나오셨습니다."))
                                getInstance().onlinePlayers[player.uniqueId.toString()]!!.clanChatMode = false
                                getInstance().onlinePlayers[player.uniqueId.toString()]!!.country = null
                            }
                        }
                    }
                    then("delete"){
                        executes {
                            if(getInstance().getPlayer(sender).country != null){
                                if(getInstance().getPlayer(sender).country!!.owner.player.uniqueId.toString() == (sender as Player).uniqueId.toString()){
                                    getInstance().server.getWorld("world")!!.entities.forEach { if(it.type == EntityType.ARMOR_STAND && it.scoreboardTags.contains("#lian_flag") && it.scoreboardTags.contains("${player.uniqueId}")) it.remove() }
                                    getInstance().countries.remove(getInstance().getPlayer(player).player.uniqueId.toString())
                                    getInstance().getPlayer(player).country!!.players.forEach { pl ->
                                        getInstance().onlinePlayers[pl.player.uniqueId.toString()]!!.clanChatMode = false
                                        getInstance().onlinePlayers[pl.player.uniqueId.toString()]!!.country = null
                                        if(pl.player.isOnline){
                                            (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(countryText("국가가 해체되었습니다."))
                                        }
                                    }
                                }
                                else {
                                    sender.sendMessage(countryText("수령이 아닙니다."))
                                }
                            }
                        }
                    }
                }
                then("all"){
                    executes {
                        sender.sendMessage(countryText("모든 국가: "))
                        getInstance().countries.forEach {
                            sender.sendMessage(" - ${ChatColor.YELLOW}${it.value.name} 국가")
                        }
                    }
                }
                then("chat"){
                    executes {
                        if(getInstance().getPlayer(player).country != null){
                            getInstance().onlinePlayers[getInstance().getPlayer(player).player.uniqueId.toString()]!!.clanChatMode = !getInstance().onlinePlayers[getInstance().getPlayer(player).player.uniqueId.toString()]!!.clanChatMode
                            sender.sendMessage(countryText("${
                                if(!getInstance().getPlayer(player).clanChatMode) "전체 채팅"
                                else "국가 채팅"
                            }을 사용합니다."))
                        }
                        else {
                            sender.sendMessage(countryText("국가에 소속되어있지 않습니다!"))
                        }
                    }
                }
                then("kick"){
                    executes {
                        sender.sendMessage(countryText("누구를 추방할 것인지 입력해주세요."))
                    }
                    then("victim" to player()){
                        executes {
                            val victim: Player by it

                            if(getInstance().getPlayer(player).country != null){
                                if(getInstance().onlinePlayers.containsKey(victim.uniqueId.toString())){
                                    if(getInstance().getPlayer(player).country!!.owner.player.uniqueId == player.uniqueId){
                                        if(getInstance().getPlayer(player).clan!!.owner.player.uniqueId != victim.uniqueId){
                                            getInstance().getPlayer(player).country!!.players.first {
                                                it.player.uniqueId == victim.uniqueId
                                            }.country = null
                                            getInstance().getPlayer(player).country!!.players.first {
                                                it.player.uniqueId == victim.uniqueId
                                            }.clanChatMode = false

                                            victim.sendMessage(countryText("국가에서 추방되었습니다."))
                                            getInstance().getPlayer(player).country!!.players.forEach {pl ->
                                                if(pl.player.isOnline){
                                                    (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(countryText("${victim.name}님이 국가에서 추방되셨습니다."))
                                                }
                                            }
                                            getInstance().getPlayer(player).country!!.players.remove(getInstance().getPlayer(player).country!!.players.first {
                                                it.player.uniqueId == victim.uniqueId
                                            })
                                            getInstance().countries[player.uniqueId.toString()] = getInstance().getPlayer(player).country!!

                                            player.sendMessage(countryText("${victim.name}님을 국가에서 추방했습니다."))
                                        }
                                        else {
                                            sender.sendMessage(countryText("현재 국가의 수령입니다. 국가를 삭제하려면 /country leave delete 명령어를 써주세요."))
                                        }
                                    }
                                    else{
                                        sender.sendMessage(countryText("수령만 추방할 수 있습니다."))
                                    }
                                }
                                else {
                                    sender.sendMessage(countryText("해당하는 이름의 플레이어가 없습니다."))
                                }
                            }
                        }
                    }
                }
                then("wardecl"){
                    executes {
                        sender.sendMessage(countryText("선전 포고할 국가의 이름을 입력해주세요."))
                    }
                    then("victim" to string(StringType.GREEDY_PHRASE)){
                        executes {
                            val victim: String by it

                            var found: Country? = null
                            getInstance().countries.forEach {
                                if(it.value.name == victim){
                                    found = it.value
                                }
                            }

                            if(found == null){
                                sender.sendMessage(countryText("해당하는 국가가 없습니다."))
                            }
                            else {
                                if(getInstance().getPlayer(sender).country == null){
                                    sender.sendMessage(countryText("소속된 국가가 없습니다."))
                                }
                                else {
                                    if(getInstance().getPlayer(sender).country!!.owner.player.uniqueId != (sender as Player).uniqueId){
                                        sender.sendMessage(countryText("수령만 선전포고할 수 있습니다."))
                                    }
                                    else {
                                        if(found!!.land != null){
                                            if(getInstance().getPlayer(sender).country!!.land != null){
                                                if(getInstance().getWar(getInstance().getPlayer(sender).country!!.owner.player.uniqueId.toString()) != null){
                                                    sender.sendMessage(countryText("이미 전쟁 중입니다."))
                                                }
                                                else {
                                                    val wt = getInstance().getPlayer(sender).country!!.lastWarDeclaratedTime
                                                    val now = Date()
                                                    val ct = (now.time - wt.time)

                                                    if(ct < 43200000L){
                                                        sender.sendMessage(countryText("선전포고까지 남은 시간: ${ct / 3600000L}시간 ${ct / 60000L}분 ${ct / 1000L}초"))
                                                    }
                                                    else {
                                                        if(getInstance().getWar(found!!.owner.player.uniqueId.toString()) != null){
                                                            sender.sendMessage(countryText("상대국이 이미 전쟁 중입니다."))
                                                        }
                                                        else {
                                                            if(!found!!.owner.player.isOnline){
                                                                sender.sendMessage(countryText("상대국의 수령이 접속 중이지 않습니다."))
                                                            }
                                                            else {
                                                                if(found!!.warDeclarationDenyCount == 3){
                                                                    getInstance().wars.add(War(Date(), Pair(getInstance().getPlayer(sender).country!!, found!!)))
                                                                    getInstance().server.broadcast(countryText("${getInstance().getPlayer(sender).country!!.name} 국가와 ${found!!.name} 국가간의 전쟁이 시작되었습니다!"))
                                                                    getInstance().getPlayer(sender).country!!.players.forEach {
                                                                        if(it.player.isOnline){
                                                                            val i = it
                                                                            getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.AQUA}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                                                        }
                                                                    }
                                                                    found!!.players.forEach {
                                                                        if(it.player.isOnline){
                                                                            val i = it
                                                                            getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.AQUA}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                                                        }
                                                                    }
                                                                }
                                                                else {
                                                                    getInstance().warDecl[found!!.owner.player.uniqueId.toString()] = getInstance().getPlayer(sender).country!!
                                                                    sender.sendMessage(countryText("선전포고 입장을 보냈습니다."))

                                                                    getInstance().countries[getInstance().getPlayer(sender).player.uniqueId.toString()]!!.lastWarDeclaratedTime = Date()

                                                                    val target = found!!.owner.player

                                                                    if(found!!.owner.player.isOnline){
                                                                        val p = getInstance().server.onlinePlayers.first { it.uniqueId == found!!.owner.player.uniqueId }
                                                                        p.sendMessage(countryText("${getInstance().getPlayer(sender).country!!.name} 국가가 선전포고했습니다. 60초 후 자동으로 거절됩니다."))
                                                                        val clickComp = net.md_5.bungee.api.chat.TextComponent("[ 전쟁 시작 ]")
                                                                        clickComp.color = ChatColor.AQUA
                                                                        clickComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("전쟁 시작하기").create())
                                                                        clickComp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/country waraccept")
                                                                        p.sendMessage(clickComp)

                                                                        getInstance().warDeclTaskId[target.uniqueId.toString()] = getInstance().server.scheduler.scheduleSyncDelayedTask(getInstance(),
                                                                            {
                                                                                getInstance().warDecl.remove(target.uniqueId.toString())
                                                                                getInstance().server.onlinePlayers.first{target.uniqueId == it.uniqueId}.sendMessage(countryText("${getInstance().getPlayer(sender).country!!.name} 국가의 선전포고를 무시했습니다."))
                                                                                getInstance().countries[target.uniqueId.toString()]!!.warDeclarationDenyCount += 1
                                                                            },
                                                                            1200L
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                player.sendMessage(countryText("국가가 땅을 소유하고있지 않습니다."))
                                            }
                                        }
                                        else {
                                            player.sendMessage(countryText("상대 국가가 땅을 소유하고있지 않습니다."))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                then("waraccept"){
                    executes {
                        val p = getInstance().getPlayer(sender)
                        if(p.country == null){
                            sender.sendMessage(countryText("소속된 국가가 없습니다."))
                        }
                        else {
                            if(getInstance().warDecl[p.country!!.owner.player.uniqueId.toString()] == null){
                                sender.sendMessage(countryText("대기 중인 선전포고가 없습니다."))
                            }
                            else {
                                if(getInstance().getWar(p.country!!.owner.player.uniqueId.toString()) != null) {
                                    sender.sendMessage(countryText("이미 다른 국가와 전쟁 중입니다."))
                                }
                                else {
                                    if(p.country!!.owner.player.uniqueId != p.player.uniqueId){
                                        sender.sendMessage(countryText("수령만 선전포고에 응할 수 있습니다."))
                                    }
                                    else {
                                        val found = getInstance().warDecl[p.country!!.owner.player.uniqueId.toString()]!!

                                        getInstance().getFlagArmorStand(getInstance().getPlayer(sender).country!!.owner.player.uniqueId.toString())?.isGlowing = true
                                        getInstance().getFlagArmorStand(found.owner.player.uniqueId.toString())?.isGlowing = true

                                        getInstance().warDecl.remove(p.country!!.owner.player.uniqueId.toString())
                                        getInstance().server.scheduler.cancelTask(getInstance().warDeclTaskId[p.country!!.owner.player.uniqueId.toString()]!!)
                                        getInstance().warDeclTaskId.remove(p.country!!.owner.player.uniqueId.toString())

                                        getInstance().wars.add(War(Date(), Pair(getInstance().getPlayer(sender).country!!, found)))
                                        getInstance().server.broadcast(countryText("${getInstance().getPlayer(sender).country!!.name} 국가와 ${found.name} 국가간의 전쟁이 시작되었습니다!"))
                                        getInstance().getPlayer(sender).country!!.players.forEach {
                                            if(it.player.isOnline){
                                                val i = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.AQUA}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                            }
                                        }

                                        found.players.forEach {
                                            if(it.player.isOnline){
                                                val i = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.AQUA}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                then("public"){
                    executes {
                        val gui = ChestGui(6, "공개 국가")

                        val pages = PaginatedPane(0, 0, 9, 5)
                        pages.populateWithItemStacks(
                            getInstance().countries.values.filter {
                                it.public && it.players.size < 8
                            }.shuffled().map {
                                val st = ItemStack(Material.PLAYER_HEAD)
                                val meta = st.itemMeta as SkullMeta

                                val c = it

                                meta.owningPlayer = it.owner.player
                                meta.displayName(text("${it.name} 국가").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
                                meta.lore(listOf(
                                    text("수령: ").color(TextColor.color(0xE0, 0xE0, 0x66)).decoration(TextDecoration.ITALIC, false).append(
                                        if(getInstance().server.onlinePlayers.any { it.uniqueId == meta.owningPlayer!!.uniqueId }){
                                            getInstance().server.onlinePlayers.first { it.uniqueId == meta.owningPlayer!!.uniqueId }.displayName().decoration(TextDecoration.ITALIC, false)
                                        }
                                        else{
                                            text(it.owner.player.name!!).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)
                                        }
                                    ),
                                    text(
                                        "현재 접속자 수: ${
                                            getInstance().server.onlinePlayers.count {
                                                c.players.map { it.player.uniqueId }.contains(it.uniqueId)
                                            }
                                        }명"
                                    ).color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false),
                                    text(
                                        "승리 수: ${c.winCount}번"
                                    ).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
                                ))

                                st.itemMeta = meta

                                st
                            }
                        )
                        pages.setOnClick { e: InventoryClickEvent ->
                            e.isCancelled = true

                            if(e.currentItem != null) {
                                val cc = (e.currentItem!!.itemMeta as SkullMeta).owningPlayer
                                val p = getInstance().getPlayer(e.whoClicked)

                                if(getInstance().countries[cc!!.uniqueId.toString()]!!.players.size < 8){
                                    if(p.clan == null && p.country == null){
                                        p.country = getInstance().countries[cc.uniqueId.toString()]
                                        getInstance().onlinePlayers[p.player.uniqueId.toString()] = p
                                        getInstance().countries[cc.uniqueId.toString()]!!.players =
                                            getInstance().countries[cc.uniqueId.toString()]!!.players.plusElement(p) as MutableList<LianPlayer>
                                        getInstance().countries[cc.uniqueId.toString()]!!.players.forEach { pl ->
                                            if(pl.player.isOnline){
                                                val p2 = getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }
                                                p2.sendMessage("${p.player.name}님이 국가에 가입했습니다. 환영 인사 한 번씩 해주세요!")
                                            }
                                        }
                                        player.sendMessage("${getInstance().countries[cc.uniqueId.toString()]!!.name} 국가에 가입했습니다.")
                                    }
                                    else {
                                        sender.sendMessage(countryText("이미 다른 클랜/국가에 속해있습니다."))
                                    }
                                }
                                else {
                                    player.sendMessage(countryText("국가 인원은 수령 포함 8명입니다."))
                                }
                            }
                        }

                        gui.addPane(pages)

                        val background = OutlinePane(0, 5, 9, 1)
                        background.addItem(GuiItem(ItemStack(Material.BLACK_STAINED_GLASS_PANE)))
                        background.setRepeat(true)
                        background.priority = Pane.Priority.LOWEST
                        background.setOnClick { event: InventoryClickEvent ->
                            event.isCancelled = true
                        }

                        gui.addPane(background)

                        val navigation = StaticPane(0, 5, 9, 1)

                        val rw = ItemStack(Material.RED_WOOL)
                        var meta = rw.itemMeta
                        meta.displayName(text("이전").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        rw.itemMeta = meta

                        navigation.addItem(
                            GuiItem(
                                rw
                            ) { event: InventoryClickEvent ->
                                event.isCancelled = true

                                if (pages.page > 0) {
                                    pages.page = pages.page - 1
                                    gui.update()
                                }
                            }, 0, 0
                        )

                        val gw = ItemStack(Material.GREEN_WOOL)
                        meta = gw.itemMeta
                        meta.displayName(text("다음").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false))
                        gw.itemMeta = meta

                        navigation.addItem(
                            GuiItem(
                                gw
                            ) { event: InventoryClickEvent ->
                                event.isCancelled = true

                                if (pages.page < pages.pages - 1) {
                                    pages.page = pages.page + 1
                                    gui.update()
                                }
                            }, 8, 0
                        )

                        val br = ItemStack(Material.BARRIER)
                        meta = br.itemMeta
                        meta.displayName(text("닫기").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false))
                        br.itemMeta = meta

                        navigation.addItem(
                            GuiItem(
                                br
                            ) { event: InventoryClickEvent ->
                                event.isCancelled = true

                                event.whoClicked.closeInventory()
                            }, 4, 0
                        )

                        gui.addPane(navigation)

                        gui.update()
                        gui.show((sender as Player))
                    }
                }
                then("togglepublic"){
                    executes {
                        if(getInstance().getPlayer(sender).country == null){
                            sender.sendMessage(countryText("국가에 소속되어있지 않습니다!"))
                        }
                        else {
                            val clan = getInstance().getPlayer(sender).country!!

                            if(clan.owner.player.uniqueId != (sender as Player).uniqueId){
                                sender.sendMessage(countryText("수령이 아닙니다."))
                            }
                            else {
                                getInstance().countries[(sender as Player).uniqueId.toString()]!!.public = !getInstance().countries[(sender as Player).uniqueId.toString()]!!.public
                                sender.sendMessage(countryText("공개 상태를 ${
                                    if(getInstance().countries[(sender as Player).uniqueId.toString()]!!.public) "공개" else "비공개"
                                }로 설정했습니다."))
                            }
                        }
                    }
                }
            }
        }
    }

    init {}
}