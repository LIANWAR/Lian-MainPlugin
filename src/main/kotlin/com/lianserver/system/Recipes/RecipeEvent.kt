package com.lianserver.system.Recipes

import com.lianserver.system.plugin.LianPlugin
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.plugin.Plugin


/***
 * @author underconnor
 */

class RecipeEvent : Listener {
    private fun getInstance(): Plugin {
        return LianPlugin.instance
    }

    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent){
        val item = e.recipe.result
        if (item.type == Material.PAPER) {
            e.isCancelled = item.itemMeta.hasLore()
        }
    }
}