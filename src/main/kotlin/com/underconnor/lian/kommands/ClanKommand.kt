package com.underconnor.lian.kommands

import com.underconnor.lian.clan.Clan
import com.underconnor.lian.plugin.KommandInterface
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Content
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.reflect.KProperty

/***
 * @author AlphaGot
 */

class ClanKommand: KommandInterface {
    private fun clanText(s: String): TextComponent = text("${getInstance().config.getString("clan_prefix")} $s")
    private fun clanTextS(s: String): String = "${getInstance().config.getString("clan_prefix")} $s"

    override fun kommand() {
        getInstance().kommand {
            register("clan") {
                then("help"){
                    executes {
                        sender.sendMessage(
                                    "${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}- 클랜 도움말 ${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-${ChatColor.WHITE}-${ChatColor.RED}-\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan info")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan create <클랜 이름>")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan invite <플레이어>")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan chat")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan leave")}\n" +
                                    "    ${clanTextS("${ChatColor.WHITE}/clan kick <플레이어>")}\n"
                        )
                    }
                }
                then("create") {
                    executes { sender.sendMessage(clanText("클랜 이름을 입력해주세요.")) }
                    then("clanName" to string()) {
                        executes { kommandContext ->
                            val hand = (sender as Player).inventory.itemInMainHand
                            if(hand.type != Material.ENCHANTED_BOOK){
                                sender.sendMessage(clanText("손에 클랜 창설권을 들어주세요."))
                            }
                            else {
                                if (false) {
                                    sender.sendMessage(clanText("손에 클랜 창설권을 들어주세요."))
                                    sender.sendMessage((sender as Player).inventory.itemInMainHand.displayName())
                                }
                                else{
                                    val clanName: String by kommandContext

                                    var clanNameTemp = clanName

                                    clanNameTemp = clanNameTemp.replace("§", " ")
                                    clanNameTemp = clanNameTemp.replace("&", " ")
                                    clanNameTemp = clanNameTemp.replace("_", " ")
                                    if(getInstance().getPlayer(sender).clan != null){
                                        sender.sendMessage(clanText("이미 클랜에 소속되어있습니다."))
                                    }
                                    else {
                                        if(getInstance().clans.none { it.name == clanNameTemp }) {
                                            val clan = Clan(getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().getPlayer(sender))], n = clanNameTemp)
                                            getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().getPlayer(sender))].clan = clan
                                            getInstance().clans.plusAssign(clan)

                                            getInstance().server.broadcast(clanText("${sender.name}님이 ${ChatColor.GREEN}${clan.name}${ChatColor.WHITE}클랜을 생성했습니다."))
                                        }
                                        else {
                                            sender.sendMessage(clanText("이미 같은 이름의 클랜이 있습니다."))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                executes { sender.sendMessage(clanText("/clan help")) }
                then("info") {
                    executes {
                        if(getInstance().getPlayer(sender).clan != null){
                            sender.sendMessage(clanText("클랜 정보"))
                            sender.sendMessage("${ChatColor.WHITE}<${ChatColor.YELLOW}${getInstance().getPlayer(sender).clan!!.name} 클랜${ChatColor.WHITE}>")
                            getInstance().getPlayer(sender).clan!!.players.forEach {
                                sender.sendMessage(" - ${ChatColor.YELLOW}${it.player.name}")
                            }
                        }
                        else {
                            sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                        }
                    }
                }
                then("invite"){
                    executes {
                        sender.sendMessage(clanText("누구를 초대할 지를 입력해주세요."))
                    }
                    then("player" to string()){
                        executes { kommandContext ->
                            if (getInstance().getPlayer(sender).clan == null) {
                                sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                            }
                            else {
                                if (getInstance().getPlayer(sender).clan!!.owner != getInstance().getPlayer(sender)) {
                                    sender.sendMessage(clanText("클랜장만 초대할 수 있습니다."))
                                } else {
                                    if(getInstance().getPlayer(sender).clan!!.players.size < 4){
                                        val targetStr: String by kommandContext
                                        val target = getInstance().server.onlinePlayers.first { it.name == targetStr }

                                        getInstance().invites[target.uniqueId.toString()] = getInstance().getPlayer(sender).clan!!
                                        sender.sendMessage(clanText("초대장을 보냈습니다."))
                                        if(getInstance().getPlayer(target).player.isOnline){
                                            val p = getInstance().server.onlinePlayers.first { it.uniqueId == getInstance().getPlayer(target).player.uniqueId }
                                            p.sendMessage(clanText("${sender.name}님이 클랜 초대장을 보냈습니다. (${getInstance().getPlayer(sender).clan!!.name} 클랜)"))
                                            val clickComp = net.md_5.bungee.api.chat.TextComponent("[ 수락 ]")
                                            clickComp.color = ChatColor.GREEN
                                            clickComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("클랜 수락하기").create())
                                            clickComp.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept")
                                            p.sendMessage(clickComp)

                                            getInstance().server.scheduler.scheduleSyncDelayedTask(getInstance(),
                                                {
                                                    getInstance().invites.remove(target.uniqueId.toString())
                                                    target.sendMessage(clanText("${sender.name}님에게서 온 클랜 초대가 만료되었습니다."))
                                                },
                                                600L)
                                        }
                                    }
                                    else {
                                        player.sendMessage(clanText("클랜 인원은 클랜장 포함 4명입니다."))
                                    }
                                }
                            }
                        }
                    }
                }
                then("accept"){
                    executes {
                        val p = getInstance().getPlayer(player)
                        if(getInstance().invites.containsKey(p.player.uniqueId.toString())){
                            if(getInstance().invites[p.player.uniqueId.toString()]!!.players.size < 4){
                                getInstance().invites.remove(player.uniqueId.toString())
                                p.clan = getInstance().invites[p.player.uniqueId.toString()]
                                getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().onlinePlayers.filter { it.player.uniqueId == p.player.uniqueId }[0])] = p
                                getInstance().clans[getInstance().clans.indexOf(getInstance().invites[p.player.uniqueId.toString()]!!)].players.plusAssign(p)
                                getInstance().clans[getInstance().clans.indexOf(getInstance().invites[p.player.uniqueId.toString()]!!)].players.forEach { pl ->
                                    if(pl.player.isOnline){
                                        val p2 = getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }
                                        p2.sendMessage("${p.player.name}님이 클랜에 가입했습니다. 환영 인사 한 번씩 해주세요!")
                                    }
                                }
                                player.sendMessage("${getInstance().clans[getInstance().clans.indexOf(getInstance().invites[p.player.uniqueId.toString()]!!)].name} 클랜에 가입했습니다.")
                            }
                            else {
                                player.sendMessage("클랜 인원은 클랜장 포함 4명입니다.")
                            }
                        }
                        else {
                            player.sendMessage("대기 중인 초대가 없습니다.")
                        }
                    }
                }
                then("leave"){
                    executes {
                        if(getInstance().getPlayer(player).clan == null){
                            player.sendMessage(clanText("나갈 클랜이 없습니다."))
                        }
                        else {
                            if(getInstance().getPlayer(player).clan!!.owner.player.uniqueId == player.uniqueId){
                                player.sendMessage(clanText("현재 클랜의 클랜장입니다. 진짜로 클랜을 삭제하시려면 /clan leave deleteclan 명령어를 입력해주세요."))
                            }
                            else {
                                getInstance().getPlayer(player).clan!!.players.forEach {pl ->
                                    if(pl.player.isOnline){
                                        (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(clanText("${player.name}님이 클랜에서 나가셨습니다."))
                                    }
                                }
                                getInstance().clans.find { it.owner.player.uniqueId == getInstance().getPlayer(player).clan!!.owner.player.uniqueId }!!.players.remove(getInstance().getPlayer(player))
                                player.sendMessage(clanText("클랜에서 나오셨습니다."))
                                getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().onlinePlayers.first { it.player.uniqueId == player.uniqueId })].clanChatMode = false
                            }
                        }
                    }
                    then("deleteclan"){
                        executes {
                            getInstance().clans.remove(getInstance().getPlayer(player).clan)
                            getInstance().getPlayer(player).clan!!.players.forEach { pl ->
                                getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(pl)].clanChatMode = false
                                getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(pl)].clan = null
                                if(pl.player.isOnline){
                                    (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(clanText("클랜이 해체되었습니다."))
                                }
                            }
                        }
                    }
                }
                then("all"){
                    executes {
                        sender.sendMessage(clanText("모든 클랜: "))
                        getInstance().clans.forEach {
                            sender.sendMessage(" - ${ChatColor.YELLOW}${it.name} 클랜")
                        }
                    }
                }
                then("chat"){
                    executes {
                        if(getInstance().getPlayer(player).clan != null){
                            getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().getPlayer(player))].clanChatMode = !getInstance().onlinePlayers[getInstance().onlinePlayers.indexOf(getInstance().getPlayer(player))].clanChatMode
                            sender.sendMessage(clanText("${
                                if(!getInstance().getPlayer(player).clanChatMode) "전체 채팅"
                                else "클랜 채팅"
                            }을 사용합니다."))
                        }
                        else {
                            sender.sendMessage(clanText("클랜에 소속되어있지 않습니다!"))
                        }
                    }
                }
                then("kick"){
                    executes {
                        sender.sendMessage("누구를 추방할 지 입력해주세요.")
                    }
                    then("victim" to player()){
                        executes { kommandContext ->
                            val victim: Player by kommandContext

                            if(getInstance().getPlayer(player).clan != null){
                                if(getInstance().getPlayer(player).clan!!.owner.player.uniqueId == player.uniqueId){
                                    getInstance().getPlayer(player).clan!!.players.first {
                                        it.player.uniqueId == victim.uniqueId
                                    }.clan = null
                                    getInstance().getPlayer(player).clan!!.players.first {
                                        it.player.uniqueId == victim.uniqueId
                                    }.clanChatMode = false

                                    victim.sendMessage(clanText("클랜에서 추방되었습니다."))
                                    getInstance().getPlayer(player).clan!!.players.forEach {pl ->
                                        if(pl.player.isOnline){
                                            (getInstance().server.onlinePlayers.first { it.uniqueId == pl.player.uniqueId }).sendMessage(clanText("${victim.name}님이 클랜에서 추방되셨습니다."))
                                        }
                                    }
                                    getInstance().getPlayer(player).clan!!.players.remove(getInstance().getPlayer(player).clan!!.players.first {
                                        it.player.uniqueId == victim.uniqueId
                                    })
                                    getInstance().clans[getInstance().clans.indexOf(getInstance().clans.first {
                                        it.owner.player.uniqueId == player.uniqueId
                                    })] = getInstance().getPlayer(player).clan!!

                                    player.sendMessage(clanText("${victim.name}님을 클랜에서 추방했습니다."))
                                }
                                else{
                                    sender.sendMessage(clanText("클랜장만 추방할 수 있습니다."))
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