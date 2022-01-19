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

package com.lianserver.system.plugin

import com.lianserver.system.Recipes.RecipeEvent
import com.lianserver.system.Recipes.RecipeObject
import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.common.LianPlayer
import com.lianserver.system.handlers.DataHandler
import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.KommandInterface
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.io.File
import java.lang.reflect.Method
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

    var countries: MutableMap<String, Country> = mutableMapOf()
    var invitesCountry: MutableMap<String, Country> = mutableMapOf()
    var invitesCountryTaskId: MutableMap<String, Int> = mutableMapOf()

    fun getPlayer(sender: CommandSender) = onlinePlayers[(sender as Player).uniqueId.toString()]!!
    fun getPlayer(sender: Player) = onlinePlayers[(sender as Player).uniqueId.toString()]!!

    fun getLandOwned(p: Pair<Int?, Int?>): Pair<Clan?, Country?>{
        var ret: Pair<Clan?, Country?> = Pair(null, null)
        clans.forEach {
            for (x in -1..1){
                for (z in -1..1){
                    if(it.value.land == Pair<Int, Int>(p.first?.plus(x) ?: 2147483647, p.second?.plus(z) ?: 2147483647)){
                        ret = Pair(it.value, null)
                        return ret
                    }
                }
            }
        }
        countries.forEach {
            for (x in -1..1){
                for (z in -1..1){
                    if(it.value.land == Pair<Int, Int>(p.first?.plus(x) ?: 2147483647, p.second?.plus(z) ?: 2147483647)){
                        ret = Pair(null, it.value)
                        return ret
                    }
                }
            }
        }
        return ret
    }

    override fun onEnable() {
        instance = this
        logger.info("${this.config.getString("admin_prefix")}")

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

        DataHandler.loadFromFile()
    }

    override fun onDisable() {
        DataHandler.saveToFile()
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
        else if(getPlayer(e.player).clanChatMode && getPlayer(e.player).country != null){
            getPlayer(e.player).country!!.players.forEach { lianPlayer ->
                if(lianPlayer.player.isOnline){
                    server.onlinePlayers.first { it.uniqueId == lianPlayer.player.uniqueId }.sendMessage(text("${ChatColor.GOLD}[국가] ${ChatColor.LIGHT_PURPLE}${e.player.name}${ChatColor.RESET}: ").append(e.message()))
                }
            }
            e.isCancelled = true
        }
    }
}