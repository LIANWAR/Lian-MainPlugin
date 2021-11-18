package com.underconnor.lian.Recipes

import com.underconnor.lian.plugin.LianPlugin
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType

/***
 * @author underconnor
 */

object RecipeObject {
    private fun getInstance(): Plugin {
        return LianPlugin.instance
    }

    fun golden_apple(): Recipe{
        val key = NamespacedKey(getInstance(), "golden_apple")
        val item = ItemStack(Material.GOLDEN_APPLE)

        val recipe = ShapedRecipe(key, item)

        recipe.shape("AGA", "GMG", "AGA")
        recipe.setIngredient('A', Material.AIR)
        recipe.setIngredient('G', Material.GOLD_INGOT)
        recipe.setIngredient('M', Material.APPLE)

        return recipe
    }

    fun original_golden_apple(): Recipe{
        val key = NamespacedKey(getInstance(), "original_golden_apple")
        val item = ItemStack(Material.PAPER)
        val meta = item.itemMeta
        
        // 조합법 팁
        // 이탈릭체 없애는법 아시는 분은 수정좀요
        meta.displayName(text("Tip!", NamedTextColor.RED))
        meta.lore(listOf(
            text("${ChatColor.RESET}황금사과 조합법 안내",NamedTextColor.GOLD),
            text("황금사과는 금 4개로 조합이 가능합니다."),
            text("${ChatColor.WHITE}█${ChatColor.GOLD}█${ChatColor.WHITE}█"),
            text("${ChatColor.GOLD}█${ChatColor.RED}█${ChatColor.GOLD}█"),
            text("${ChatColor.WHITE}█${ChatColor.GOLD}█${ChatColor.WHITE}█")
        ))

        item.itemMeta = meta
        val recipe = ShapedRecipe(key, item)

        recipe.shape("GGG", "GMG", "GGG")
        recipe.setIngredient('G', Material.GOLD_INGOT)
        recipe.setIngredient('M', Material.APPLE)

        return recipe
    }

    // 화살 간단 조합법 코드 (누가 최적화좀)
    fun potion_arrow(): ArrayList<Recipe>{
        val potion_arrow_list:ArrayList<Recipe> = ArrayList()
        for (effect in PotionType.values()){
            for (extended:Boolean in listOf(true,false)){
                for (upgraded:Boolean in listOf(true,false)){
                    val key = NamespacedKey(getInstance(), "tipped_arrow_${effect}_${extended}_${upgraded}")
                    val item = ItemStack(Material.TIPPED_ARROW)
                    val meta = item.itemMeta as PotionMeta

                    if (!effect.isExtendable){ meta.basePotionData = PotionData(effect) }
                    else if (!effect.isUpgradeable){ meta.basePotionData = PotionData(effect,extended,false) }
                    else {
                        if (upgraded) {meta.basePotionData = PotionData(effect,false,upgraded) }
                        else{meta.basePotionData = PotionData(effect,extended,false)}
                    }

                    item.itemMeta = meta

                    val splash_potion = ItemStack(Material.SPLASH_POTION)
                    splash_potion.itemMeta = meta

                    val recipe = ShapedRecipe(key, item)
                    recipe.shape("AAA", "APA", "AAA")
                    recipe.setIngredient('A', Material.ARROW)
                    recipe.setIngredient('P', splash_potion)
                    potion_arrow_list.add(recipe)
                }
            }
        }
        return potion_arrow_list
    }
}
