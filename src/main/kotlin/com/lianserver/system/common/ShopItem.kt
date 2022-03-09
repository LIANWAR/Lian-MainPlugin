package com.lianserver.system.common

import com.google.gson.annotations.SerializedName
import net.kyori.adventure.text.Component
import org.bukkit.inventory.meta.ItemMeta

data class ShopItem(
    @SerializedName("name")
    val name: Component,
    @SerializedName("lore")
    val lore: Array<Component>,
    @SerializedName("price")
    var price: Int /* cash */,
    @SerializedName("owner")
    val owner: String /* if Admin, admin else player uuid */,
    @SerializedName("item")
    val item: String,
    @SerializedName("meta")
    val meta: ItemMeta
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShopItem

        if (name != other.name) return false
        if (!lore.contentEquals(other.lore)) return false
        if (price != other.price) return false
        if (owner != other.owner) return false
        if (item != other.item) return false
        if (meta != other.meta) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + lore.contentHashCode()
        result = 31 * result + price
        result = 31 * result + owner.hashCode()
        result = 31 * result + item.hashCode()
        result = 31 * result + meta.hashCode()
        return result
    }
}
