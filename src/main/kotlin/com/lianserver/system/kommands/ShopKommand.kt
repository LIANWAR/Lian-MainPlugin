package com.lianserver.system.kommands

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.lianserver.system.interfaces.KommandInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.kommand.Kommand.Companion.register
import org.bukkit.event.inventory.InventoryClickEvent

class ShopKommand: KommandInterface, PrefixedTextInterface {
    override fun kommand() {
        register(getInstance(), "cshop", "캐시상점"){
            executes {
                val guiCashShop = ChestGui.load(getInstance(), getInstance().getResource("")!!)
                guiCashShop!!.setOnGlobalClick { e: InventoryClickEvent ->
                    e.isCancelled = true
                }
            }
        }
    }

    init {}
}