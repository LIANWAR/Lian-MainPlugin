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
import java.io.File

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
        MainKommand.MainKommand()
        //debugKommand.debugKommand()

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

        val golden_apple = RecipeObject.golden_apple()
        val original_golden_apple = RecipeObject.original_golden_apple()
        val tipped_arrow:ArrayList<Recipe> = RecipeObject.potion_arrow()

        server.addRecipe(golden_apple)
        server.addRecipe(original_golden_apple)
        for (potion_arrow in tipped_arrow) {
            server.addRecipe(potion_arrow)
        }
    }
}