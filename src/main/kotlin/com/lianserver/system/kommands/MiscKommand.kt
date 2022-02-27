package com.lianserver.system.kommands

import com.lianserver.system.interfaces.KommandInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.kommand.Kommand.Companion.register
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class MiscKommand: KommandInterface, PrefixedTextInterface {
    override fun kommand() {
        register(getInstance(), "license") {
            executes {
                sender.sendMessage(adminText("Lian-Server/Lian-MainPlugin ${getInstance().description.version} - GNU 일반 공중 사용 허가서 3.0"))
                sender.sendMessage(adminText("의존 라이브러리: "))
                sender.sendMessage(adminText(" * monun/kommand: 2.6.6"))
                sender.sendMessage(adminText(" * org.reflections:reflections: 0.10.2 (GitHub에 없음)"))
                sender.sendMessage(adminText(" * stefvanschie/IF: 0.10.4"))
                sender.sendMessage(adminText(" * WesJD/AnvilGUI: 1.5.3"))
                sender.sendMessage(adminText("리소스팩 - AlphaGot 제작"))
                sender.sendMessage(adminText("폰트 라이센스: "))
                sender.sendMessage(adminText(" * 카페24 써라운드체 - SIL 자유 폰트 라이선스"))
            }
        }
        register(getInstance(), "lotto"){
            executes {
                var amount = 0
                for (item in player.inventory.contents.filterNotNull()) {
                    if (item.type == Material.IRON_INGOT) {
                        amount += item.amount
                    }
                }
                if(amount >= 8){
                    for(i in 0..7){
                        player.inventory.removeItem(ItemStack(Material.IRON_INGOT))
                    }

                    val stack = when(Random.nextInt(0, 1000)){
                        in 0..9 -> ItemStack(Material.NETHERITE_INGOT)
                        in 10..39 -> ItemStack(Material.DIAMOND, 8)
                        in 40..399 -> ItemStack(Material.DIAMOND)
                        else -> ItemStack(Material.AIR)
                    }

                    player.inventory.addItem(stack)

                    if(stack.type == Material.AIR){
                        player.sendMessage(userText("꽝!"))
                    }
                    else {
                        player.sendMessage(
                            when(stack){
                                ItemStack(Material.NETHERITE_INGOT) -> userText("1등 당첨!")
                                ItemStack(Material.DIAMOND, 8) -> userText("2등 당첨!")
                                ItemStack(Material.DIAMOND, 1) -> userText("3등 당첨!")
                                else -> userText("버그 당첨!")
                            }
                        )
                    }
                }
                else {
                    player.sendMessage(userText("철괴가 모자랍니다."))
                }
            }
        }
    }

    init {}
}