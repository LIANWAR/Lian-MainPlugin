package com.lianserver.system.Recipes

import com.lianserver.system.plugin.LianPlugin
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
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
import java.lang.reflect.Method

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
        meta.displayName(text("Tip!", NamedTextColor.RED).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
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

    fun clan_ticket(): Recipe {
        val key = NamespacedKey(getInstance(), "clan_ticket")
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta

        // 조합법 팁
        meta.displayName(text("클랜 창설권", NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))

        item.itemMeta = meta
        val recipe = ShapedRecipe(key, item)

        recipe.shape("SDS", "DBD", "SDS")
        recipe.setIngredient('S', Material.STONE_SWORD)
        recipe.setIngredient('D', Material.DIAMOND)
        recipe.setIngredient('B', Material.BOOK)

        return recipe
    }

    fun land_doc(): Recipe {
        val key = NamespacedKey(getInstance(), "land_document")
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta

        // 조합법 팁
        meta.displayName(text("땅문서", NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))

        item.itemMeta = meta
        val recipe = ShapedRecipe(key, item)

        recipe.shape("SSS", "DBD", "RIR")
        recipe.setIngredient('S', Material.DIRT)
        recipe.setIngredient('D', Material.DIAMOND)
        recipe.setIngredient('B', Material.WRITABLE_BOOK)
        recipe.setIngredient('R', Material.STONE)
        recipe.setIngredient('I', Material.IRON_SHOVEL)

        return recipe
    }

    // 화살 간단 조합법 코드 (누가 최적화좀)
    fun potion_arrow(): ArrayList<Recipe>{
        val potionArrowList: ArrayList<Recipe> = arrayListOf()
        for (effect in PotionType.values()){
            for (extended: Boolean in listOf(true,false)){
                for (upgraded: Boolean in listOf(true,false)){
                    val key = NamespacedKey(getInstance(), "tipped_arrow_${effect}_${extended}_${upgraded}")
                    val item = ItemStack(Material.TIPPED_ARROW)
                    val meta = item.itemMeta as PotionMeta

                    if (!effect.isExtendable){ meta.basePotionData = PotionData(effect) }
                    else if (!effect.isUpgradeable){ meta.basePotionData = PotionData(effect, extended,false) }
                    else {
                        if (upgraded) { meta.basePotionData = PotionData(effect,false, upgraded) }
                        else { meta.basePotionData = PotionData(effect, extended,false) }
                    }

                    item.itemMeta = meta

                    val splashPotion = ItemStack(Material.SPLASH_POTION)
                    splashPotion.itemMeta = meta

                    val recipe = ShapedRecipe(key, item)
                    recipe.shape("AAA", "APA", "AAA")
                    recipe.setIngredient('A', Material.ARROW)
                    recipe.setIngredient('P', splashPotion)
                    potionArrowList.add(recipe)
                }
            }
        }
        return potionArrowList
    }

    fun getRecipes(): Array<Array<Any>> {
        var ret: Array<Array<Any>> = arrayOf()

        this.javaClass.declaredMethods.forEach {
            if(it.genericReturnType.typeName.contains("Recipe", ignoreCase = true)) ret = ret.plusElement(
                arrayOf(
                    it,
                    it.genericReturnType.typeName.contains("Array", ignoreCase = true)
                )
            )
        }
        getInstance().logger.info(ret.toString())
        return ret
    }
}
