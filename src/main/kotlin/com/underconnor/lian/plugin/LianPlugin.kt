/*
 * Copyright (c) 2021 underconnor, AlphaGot
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.underconnor.lian.plugin

import com.underconnor.lian.Recipes.RecipeEvent
import com.underconnor.lian.Recipes.RecipeObject
import com.underconnor.lian.clan.Clan
import com.underconnor.lian.common.LianPlayer
import com.underconnor.lian.events.SampleEvent
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.io.File
import java.lang.reflect.Method
import java.util.*
import java.util.logging.Level
import kotlin.collections.ArrayList

/***
 * @author underconnor, AlphaGot
 */

class LianPlugin : JavaPlugin(), Listener {

    companion object {
        lateinit var instance: LianPlugin
            private set
    }

    private val configFile = File(dataFolder, "config.yml")
    val onlinePlayers: MutableMap<String, LianPlayer> = mutableMapOf()
    var clans: MutableMap<String, Clan> = mutableMapOf()
    var invites: MutableMap<String, Clan> = mutableMapOf()
    var invitesTaskId: MutableMap<String, Int> = mutableMapOf()

    fun getPlayer(sender: CommandSender) = onlinePlayers[(sender as Player).uniqueId.toString()]!!
    fun getPlayer(sender: Player) = onlinePlayers[(sender as Player).uniqueId.toString()]!!

    fun getPlayerByFile(p: String  /* UUID */): LianPlayer {
        val f = File("plugins/LianMain/players/${p}.txt").readText().split("\n")
        onlinePlayers[p] = LianPlayer(server.offlinePlayers.filter { it.uniqueId.toString() == p }[0])
        return LianPlayer(server.offlinePlayers.filter { it.uniqueId.toString() == p }[0])
    }

    override fun onEnable() {
        instance = this
        logger.info("${this.config.getString("admin_prefix")}")
        server.pluginManager.registerEvents(SampleEvent(), this)

        var reflections = Reflections("com.underconnor.lian.kommands")

        reflections.getSubTypesOf(
            KommandInterface::class.java
        )?.forEach { clazz ->
            logger.info(clazz.name)

            clazz.getDeclaredConstructor().trySetAccessible()
            clazz.getDeclaredConstructor().newInstance().kommand()
        }

        reflections = Reflections("com.underconnor.lian.handlers")

        reflections.getSubTypesOf(
            HandlerInterface::class.java
        )?.forEach { clazz ->
            logger.info(clazz.name)

            clazz.getDeclaredConstructor().trySetAccessible()
            server.pluginManager.registerEvents(clazz.getDeclaredConstructor().newInstance(), this)
        }

        // Recipe Remove 처리
        // 나중에 리스트로 한번에 처리할 예정
        val it = Bukkit.getServer().recipeIterator()
        var recipe: Recipe?
        while (it.hasNext()) {
            recipe = it.next()
            if (recipe != null && recipe.result.type == Material.GOLDEN_APPLE) {
                it.remove()
            }
        }
        server.pluginManager.registerEvents(RecipeEvent(), this)

        for(i in RecipeObject.getRecipes()){
            if(i[1] as Boolean){
                val j: Any = (i[0] as Method).invoke(RecipeObject)

                if(j !is ArrayList<*>) { // 혹1시 모를 다른 타입 메서드 넘어오는 거 처리
                    logger.info(j.javaClass.name)
                    throw RuntimeException("레시피 등록 중 형변환에 실패했습니다.")
                }

                for(g in j){
                    server.addRecipe(g as Recipe?)
                }
            }
            else{
                server.addRecipe(((i[0] as Method).invoke(RecipeObject) as Recipe?))
            }
        }

        server.pluginManager.registerEvents(this, this)

        val testPluginDir = File("plugins/LianMain")
        if(!testPluginDir.exists()){
            testPluginDir.mkdir()
        }

        var playerDir = File("plugins/LianMain/clans")
        if(!(playerDir.exists() && playerDir.isDirectory)){
            logger.warning("플레이어 저장 경로가 없거나 폴더가 아닙니다.")

            if(!playerDir.isDirectory){
                logger.log(Level.OFF, "플레이어 저장 경로와 같은 이름의 파일이 있습니다.")
                isEnabled = false
            }
            else {
                playerDir.mkdir()
            }
        }
        else {
            playerDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".txt")){
                    if(onlinePlayers.none { it.value.player.uniqueId.toString() == file.readText().split("\n")[0] }){
                        val f = file.readText().split("\n")
                        val c = LianPlayer(server.getOfflinePlayer(UUID.fromString(f[0])))
                        logger.info(clans.toString())

                        c.clan = if (clans.any { it.value.owner.player.uniqueId.toString() == f[1] }) {
                            clans[f[1]]
                        }
                        else null

                        c.ownedLands = listOf()
                        f.subList(3, f.size).forEach {
                            val s = it.split(" ")
                            c.ownedLands = c.ownedLands.plusElement(Pair(s[0].toInt(), s[1].toInt()))
                        }

                        onlinePlayers[c.player.uniqueId.toString()] = c
                    }
                }
            }
        }

        val clanDir = File("plugins/LianMain/clans")
        if(!(clanDir.exists() && clanDir.isDirectory)){
            logger.warning("클랜 저장 경로가 없거나 폴더가 아닙니다.")

            if(!clanDir.isDirectory){
                logger.log(Level.OFF, "클랜 저장 경로와 같은 이름의 파일이 있습니다.")
                isEnabled = false
            }
            else {
                clanDir.mkdir()
            }
        }
        else {
            clanDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".txt")){
                    val c = file.readText().split("\n")

                    logger.info(c.size.toString())
                    clans[c[0].trim()] = Clan(
                        onlinePlayers[c[0].trim()]!!,
                        c.subList(2, c.size).map { el ->
                            logger.info(el)
                            onlinePlayers[el.trim()]!!
                        } as MutableList<LianPlayer>,
                        n = c[1]
                    )
                }
            }
        }

        playerDir = File("plugins/LianMain/clans")
        if(!(playerDir.exists() && playerDir.isDirectory)){
            logger.warning("플레이어 저장 경로가 없거나 폴더가 아닙니다.")

            if(!playerDir.isDirectory){
                logger.log(Level.OFF, "플레이어 저장 경로와 같은 이름의 파일이 있습니다.")
                isEnabled = false
            }
            else {
                playerDir.mkdir()
            }
        }
        else {
            playerDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".txt")){
                    if(onlinePlayers.containsKey(file.readText().split("\n")[0].trim())){
                        val f = file.readText().split("\n")
                        if(f[1].trim() != (onlinePlayers[f[0].trim()]!!.clan?.owner?.player?.uniqueId.toString())){
                            onlinePlayers[f[0].trim()]!!.clan = clans[f[1].trim()]
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        val clanDir = File("plugins/LianMain/clans")
        if(!(clanDir.exists() && clanDir.isDirectory)){
            logger.warning("클랜 저장 경로가 없거나 폴더가 아닙니다.")

            if(!clanDir.isDirectory){
                logger.log(Level.OFF, "클랜 저장 경로와 같은 이름의 파일이 있습니다.")
                isEnabled = false
            }
            else {
                clanDir.mkdir()
            }
        }
        else {
            clans.forEach {
                File("plugins/LianMain/clans/${it.value.owner.player.uniqueId}.txt").writeText(it.toString())
            }
        }

        val playerDir = File("plugins/LianMain/clans")
        if(!(playerDir.exists() && playerDir.isDirectory)){
            logger.warning("플레이어 저장 경로가 없거나 폴더가 아닙니다.")

            if(!playerDir.isDirectory){
                logger.log(Level.OFF, "플레이어 저장 경로와 같은 이름의 파일이 있습니다.")
                isEnabled = false
            }
            else {
                playerDir.mkdir()
            }
        }
        else {
            onlinePlayers.forEach {
                File("plugins/LianMain/players/${it.value.player.uniqueId}.txt").writeText(it.value.toString())
            }
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        if(onlinePlayers.none { it.value.player.uniqueId == e.player.uniqueId }){
            onlinePlayers[e.player.uniqueId.toString()] = LianPlayer(e.player)
        }
    }

    @EventHandler
    fun onChat(e: AsyncChatEvent){
        if(getPlayer(e.player).clanChatMode && getPlayer(e.player).clan != null){
            getPlayer(e.player).clan!!.players.forEach { lianPlayer ->
                if(lianPlayer.player.isOnline){
                    server.onlinePlayers.first { it.uniqueId == lianPlayer.player.uniqueId }.sendMessage(text("${ChatColor.AQUA}[클랜] ${ChatColor.LIGHT_PURPLE}${e.player.name}${ChatColor.RESET}: ").append(e.message()))
                }
            }
            e.isCancelled = true
        }
    }
}