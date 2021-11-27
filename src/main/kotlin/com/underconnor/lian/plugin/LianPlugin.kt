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

import com.underconnor.lian.events.SampleEvent
import com.underconnor.lian.kommands.MainKommand
import com.underconnor.lian.Recipes.RecipeEvent
import com.underconnor.lian.Recipes.RecipeObject
import com.underconnor.lian.kommands.debugKommand
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.Recipe
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections
import java.io.File
import java.lang.reflect.Method

/***
 * @author underconnor
 */

class LianPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: LianPlugin
            private set
    }

    private val configFile = File(dataFolder, "config.yml")

    override fun onEnable() {
        instance = this
        logger.info("${this.config.getString("admin_prefix")}")
        server.pluginManager.registerEvents(SampleEvent(), this)

        val reflections = Reflections("com.underconnor.lian.kommands")

        reflections.getSubTypesOf(
            KommandInterface::class.java
        )?.forEach {
            it.newInstance().kommand()
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
                val j: Any = ((i[0] as Method).invoke(RecipeObject))

                if(j !is Array<*>) throw RuntimeException("레시피 등록 중 형변환에 실패했습니다.")

                for(g in j){
                    server.addRecipe(g as Recipe?)
                }
            }
            else{
                server.addRecipe(i[0] as Recipe?)
            }
        }
    }
}