/*
 * Copyright (c) 2021 underconnor
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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.underconnor.lian.events.SampleEvent
import com.underconnor.lian.Recipes.RecipeEvent
import com.underconnor.lian.Recipes.RecipeObject
import com.underconnor.lian.clan.Clan
import com.underconnor.lian.common.LianPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.io.File
import java.lang.reflect.Method
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
    val onlinePlayers: ArrayList<LianPlayer> = arrayListOf()
    var clans: ArrayList<Clan> = arrayListOf()

    lateinit var gson: Gson

    fun getPlayer(sender: CommandSender) = onlinePlayers.filter { it.player.uniqueId == (sender as Player).uniqueId }[0]
    fun getPlayer(sender: Player) = onlinePlayers.filter { it.player.uniqueId == sender.uniqueId }[0]

    override fun onEnable() {
        gson = GsonBuilder().setPrettyPrinting().create()

        instance = this
        logger.info("${this.config.getString("admin_prefix")}")
        server.pluginManager.registerEvents(SampleEvent(), this)

        val reflections = Reflections("com.underconnor.lian.kommands")

        reflections.getSubTypesOf(
            KommandInterface::class.java
        )?.forEach { clazz ->
            logger.info(clazz.name)

            clazz.getConstructor().trySetAccessible()
            clazz.getDeclaredConstructor().newInstance().kommand()
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
            clanDir.listFiles()?.forEach {
                if(it.name.endsWith(".json")){
                    clans.plusAssign(gson.fromJson(it.bufferedReader(), Clan::class.java))
                }
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
            playerDir.listFiles()?.forEach {
                if(it.name.endsWith(".json")){
                    onlinePlayers.plusAssign(gson.fromJson(it.bufferedReader(), LianPlayer::class.java))
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
                File("plugins/LianMain/clans/${it.name}.json").writeText(
                    gson.toJson(it, Clan::class.java)
                )
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
                File("plugins/LianMain/players/${it.player.uniqueId}.json").writeText(
                    gson.toJson(it, LianPlayer::class.java)
                )
            }
        }
    }

    @EventHandler
    fun onJoin(e: PlayerJoinEvent){
        onlinePlayers.plusAssign(LianPlayer(e.player))
        logger.info("joined")
        logger.info(onlinePlayers.toString())
    }

    @EventHandler
    fun onLeft(e: PlayerQuitEvent){
        onlinePlayers.remove(onlinePlayers.filter { it.player.uniqueId == e.player.uniqueId }[0])
    }
}