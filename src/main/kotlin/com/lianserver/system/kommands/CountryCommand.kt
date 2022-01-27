package com.lianserver.system.kommands

import com.lianserver.system.common.Country
import com.lianserver.system.common.LianPlayer
import com.lianserver.system.common.War
import com.lianserver.system.interfaces.KommandInterface
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjuster
import java.util.*
import kotlin.math.sign

/***
 * @author AlphaGot
 */

class CountryCommand: KommandInterface {
    override fun kommand() {
        getInstance().kommand {
            register("country") {
                then("help"){
                    executes {
                        sender.sendMessage(
                                    "${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}- 클랜 도움말 ${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country info")}\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country create <클랜 이름>")}\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country invite <플레이어>")}\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country chat")}\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country leave")}\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country kick <플레이어>")}\n" +
                                    "    ${countryTextS("${ChatColor.WHITE}/country wardecl <클랜 이름>")}\n"
                        )
                    }
                }
                executes { sender.sendMessage(countryText("/country help")) }
                then("info") {
                    executes {
                        if(getInstance().getPlayer(sender).country != null){
                            sender.sendMessage(countryText("국가 정보"))
                            sender.sendMessage("${ChatColor.WHITE}<${ChatColor.YELLOW}${getInstance().getPlayer(sender).country!!.name} 국가${ChatColor.WHITE}>")
                            getInstance().getPlayer(sender).country!!.players.forEach {
                                sender.sendMessage(" - ${ChatColor.YELLOW}${it.player.name}")
                            }
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
                            if(getInstance().invitesCountry[p.player.uniqueId.toString()]!!.players.size < 4){
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
                            }
                            else {
                                player.sendMessage("국가 인원은 수령 포함 8명입니다.")
                            }
                        }
                        else {
                            player.sendMessage("대기 중인 초대가 없습니다.")
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
                                getInstance().onlinePlayers[getInstance().getPlayer(player).country!!.owner.player.uniqueId.toString()]!!.clanChatMode = false
                            }
                        }
                    }
                    then("deleteclan"){
                        executes {
                            getInstance().countries.remove(getInstance().getPlayer(player).player.uniqueId.toString())
                            getInstance().getPlayer(player).country!!.players.forEach { pl ->
                                getInstance().onlinePlayers[pl.player.uniqueId.toString()]!!.clanChatMode = false
                                getInstance().onlinePlayers[pl.player.uniqueId.toString()]!!.country = null
                                if(pl.player.isOnline){
                                    (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(countryText("국가가 해체되었습니다."))
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
                        sender.sendMessage(countryText("누구를 추방할 지 입력해주세요."))
                    }
                    then("victim" to player()){
                        executes {
                            val victim: Player by it

                            if(getInstance().getPlayer(player).country != null){
                                if(getInstance().getPlayer(player).country!!.owner.player.uniqueId == player.uniqueId){
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
                                else{
                                    sender.sendMessage(countryText("수령만 추방할 수 있습니다."))
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
                                        if(getInstance().getWar(getInstance().getPlayer(sender).country!!.owner.player.uniqueId.toString()) != null){
                                            sender.sendMessage(countryText("이미 전쟁 중입니다."))
                                        }
                                        else {
                                            val wt = getInstance().getPlayer(sender).country!!.lastWarDeclaratedTime
                                            val now = Date()
                                            val ct = Date(now.time - wt.time)

                                            if(ct.time < 43200000){
                                                getInstance().logger.info(ct.toString())
                                                getInstance().logger.info((now.time - wt.time).toString())
                                                getInstance().logger.info((now.time).toString())
                                                getInstance().logger.info((wt.time).toString())
                                                sender.sendMessage(countryText("선전포고까지 남은 시간: ${SimpleDateFormat("H시간 m분 s초").format(ct)}"))
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
                                                                    getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.RED}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                                                }
                                                            }
                                                            found!!.players.forEach {
                                                                if(it.player.isOnline){
                                                                    val i = it
                                                                    getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.RED}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
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
                                                                clickComp.color = ChatColor.RED
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

                                        getInstance().wars.add(War(Date(), Pair(getInstance().getPlayer(sender).country!!, found)))
                                        getInstance().server.broadcast(countryText("${getInstance().getPlayer(sender).country!!.name} 국가와 ${found.name} 국가간의 전쟁이 시작되었습니다!"))
                                        getInstance().getPlayer(sender).country!!.players.forEach {
                                            if(it.player.isOnline){
                                                val i = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.RED}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                            }
                                        }

                                        found.players.forEach {
                                            if(it.player.isOnline){
                                                val i = it
                                                getInstance().server.onlinePlayers.first { it.uniqueId == i.player.uniqueId }.sendTitle("${ChatColor.RED}${ChatColor.BOLD}전쟁 시작", "", 15, 50, 25)
                                            }
                                        }
                                    }
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