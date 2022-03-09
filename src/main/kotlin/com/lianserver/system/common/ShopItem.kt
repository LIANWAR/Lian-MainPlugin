package com.lianserver.system.common

import com.google.gson.annotations.SerializedName
import net.kyori.adventure.text.Component

data class ShopItem(
    @SerializedName("name")
    val name: Component,
    @SerializedName("lore")
    val lore: Component,
    @SerializedName("price")
    var price: Int /* cash */,
    @SerializedName("owner")
    val owner: String // if Admin, admin else player uuid
)
