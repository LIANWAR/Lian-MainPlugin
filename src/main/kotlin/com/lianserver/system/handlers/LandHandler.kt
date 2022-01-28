package com.lianserver.system.handlers

import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.interfaces.HandlerInterface
import com.lianserver.system.interfaces.PrefixedTextInterface
import io.github.monun.tap.fake.invisible
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack


class LandHandler: HandlerInterface, PrefixedTextInterface {
    @EventHandler
    fun onPlace(e: BlockPlaceEvent){
        if(getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(c.second!!.owner.player.uniqueId != getInstance().getPlayer(e.player).country!!.owner.player.uniqueId && !e.player.isOp){
                    if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(e.player).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(e.player).country!!)){
                        e.isCancelled = true
                        e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlaceM(e: BlockMultiPlaceEvent){
        if(getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.blockPlaced.chunk.x, e.blockPlaced.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(getInstance().getPlayer(e.player).country != null){
                    if(c.second!!.owner.player.uniqueId != getInstance().getPlayer(e.player).country!!.owner.player.uniqueId && !e.player.isOp){
                        if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(e.player).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(e.player).country!!)){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                        }
                    }
                }
                else {
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent){
        if(getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(getInstance().getPlayer(e.player).country != null){
                    if(c.second!!.owner.player.uniqueId != getInstance().getPlayer(e.player).country!!.owner.player.uniqueId && !e.player.isOp){
                        if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(e.player).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(e.player).country!!)){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                        }
                        else if(arrayOf(Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.SHULKER_BOX).contains(e.block.type)){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("상자류 블록은 부술 수 없습니다."))
                        }
                    }
                }
                else {
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun onInteract(e: PlayerInteractEvent){
        if(getInstance().getLandOwned(Pair(e.clickedBlock?.chunk?.x, e.clickedBlock?.chunk?.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.clickedBlock?.chunk?.x, e.clickedBlock?.chunk?.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if (getInstance().getPlayer(e.player).country != null) {
                    if(c.second!!.owner.player.uniqueId != getInstance().getPlayer(e.player).country!!.owner.player.uniqueId && !e.player.isOp){
                        if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(e.player).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(e.player).country!!)){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                        }
                        else {
                            if(arrayOf(Material.CHEST, Material.ENDER_CHEST, Material.SHULKER_BOX).contains(e.clickedBlock?.type)){
                                e.isCancelled = true
                                e.player.sendMessage(countryText("잠겨있습니다."))
                            }
                        }
                    }
                }
                else {
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler
    fun claimHandler(e: PlayerInteractEvent){
        if(e.clickedBlock != null){
            if(e.item?.type == Material.ENCHANTED_BOOK){
                if(e.item!!.hasItemMeta()){
                    if(e.item!!.itemMeta.displayName() == text("땅문서").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)){
                        var cond = true
                        var tT: MutableList<Pair<Clan?, Country?>> = mutableListOf()
                        for(nX in -1..1){
                            for(nZ in -1..1){
                                cond = cond && (getInstance().getLandOwned(Pair(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ)) == Pair<Clan?, Country?>(null, null))
                                tT.add(getInstance().getLandOwned(Pair(e.clickedBlock!!.chunk.x + nX, e.clickedBlock!!.chunk.z + nZ)))
                            }
                        }

                        if(cond){
                            if(getInstance().getPlayer(e.player).clan != null || getInstance().getPlayer(e.player).country != null){
                                if(getInstance().getPlayer(e.player).clan != null){
                                    if(getInstance().getPlayer(e.player).clan!!.land != null){
                                        e.player.sendMessage(clanText("이미 클랜이 땅을 갖고 있습니다."))
                                    }
                                    else if(getInstance().getPlayer(e.player).clan!!.owner.player.uniqueId != e.player.uniqueId){
                                        e.player.sendMessage(clanText("클랜장이 아닙니다."))
                                    }
                                    else {
                                        val locMulX = 1
                                        val locMulZ = 1

                                        val armorStand = e.player.world.spawnEntity(Location(e.player.world, (e.clickedBlock!!.chunk.x * 16.0) + (8.0 * locMulX), e.clickedBlock!!.y.toDouble() + 1.0, (e.clickedBlock!!.chunk.z * 16.0) + (8.0 * locMulZ)), EntityType.ARMOR_STAND) as ArmorStand

                                        armorStand.addScoreboardTag("lian_flag")
                                        armorStand.addScoreboardTag(getInstance().getPlayer(e.player).clan!!.owner.player.uniqueId.toString())

                                        armorStand.customName(text("깃발").color(TextColor.color(255, 128, 128)))

                                        armorStand.isPersistent = true
                                        armorStand.invisible = true
                                        armorStand.isCustomNameVisible = true
                                        armorStand.isInvulnerable = true
                                        armorStand.setAI(false)

                                        armorStand.equipment.helmet = ItemStack(Material.REPEATING_COMMAND_BLOCK)

                                        EquipmentSlot.values().forEach {
                                            armorStand.setDisabledSlots(it)
                                        }

                                        var hP = armorStand.headPose
                                        hP = hP.setX(Math.toRadians(180.0))
                                        armorStand.headPose = hP

                                        getInstance().clans[e.player.uniqueId.toString()]!!.land = Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)
                                        e.player.sendMessage(clanText("클랜의 땅을 설정했습니다."))
                                        e.item!!.subtract(1)
                                    }
                                }
                                else {
                                    if(getInstance().getPlayer(e.player).country!!.land != null){
                                        e.player.sendMessage(countryText("이미 국가가 땅을 갖고 있습니다."))
                                    }
                                    else if(getInstance().getPlayer(e.player).country!!.owner.player.uniqueId != e.player.uniqueId){
                                        e.player.sendMessage(countryText("수령이 아닙니다."))
                                    }
                                    else {
                                        val locMulX = 1
                                        val locMulZ = 1

                                        val armorStand = e.player.world.spawnEntity(Location(e.player.world, (e.clickedBlock!!.chunk.x * 16.0) + (7.0 * locMulX), e.clickedBlock!!.y.toDouble(), (e.clickedBlock!!.chunk.z * 16.0) + (7.0 * locMulZ)), EntityType.ARMOR_STAND) as ArmorStand

                                        armorStand.addScoreboardTag("lian_flag")
                                        armorStand.addScoreboardTag(getInstance().getPlayer(e.player).country!!.owner.player.uniqueId.toString())

                                        armorStand.customName(text("깃발").color(TextColor.color(255, 128, 128)))

                                        armorStand.isPersistent = true
                                        armorStand.invisible = true
                                        armorStand.isCustomNameVisible = true
                                        armorStand.isInvulnerable = true

                                        armorStand.equipment.helmet = ItemStack(Material.REPEATING_COMMAND_BLOCK)

                                        EquipmentSlot.values().forEach {
                                            armorStand.setDisabledSlots(it)
                                        }

                                        armorStand.headPose.x = 180.0

                                        getInstance().countries[e.player.uniqueId.toString()]!!.land = Pair(e.clickedBlock!!.chunk.x, e.clickedBlock!!.chunk.z)
                                        e.player.sendMessage(countryText("국가의 땅을 설정했습니다."))
                                        e.item!!.subtract(1)
                                    }
                                }
                            }
                            else {
                                e.player.sendMessage(countryText("클랜 또는 국가가 없습니다."))
                            }
                        }
                        else {
                            e.player.sendMessage(clanText("소유하려는 땅 및 그 주변이 다음 클랜 또는 국가의 땅입니다."))

                            tT = tT.distinct() as MutableList<Pair<Clan?, Country?>>
                            tT.forEach {
                                if(it.first != null){
                                    e.player.sendMessage(clanText(" - ${it.first!!.name} 클랜"))
                                }
                                else {
                                    e.player.sendMessage(clanText(" - ${it.second!!.name} 국가"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onLavaPlace(e: PlayerBucketEmptyEvent){
        if(getInstance().getPlayer(e.player).country != null){
            val ld = getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z))

            if(ld != Pair<Clan?, Country?>(null, null)){
                if(ld.first != null){
                    if(ld.first!!.owner.player.uniqueId != getInstance().getPlayer(e.player).clan!!.owner.player.uniqueId && !e.player.isOp){
                        e.isCancelled = true
                        e.player.sendMessage(clanText("이 땅은 ${ld.first!!.name} 클랜의 땅입니다."))
                    }
                }
                else if(ld.second != null){
                    if(ld.second != getInstance().getPlayer(e.player).country && !e.player.isOp){
                        val w = getInstance().getWar(ld.second!!.owner.player.uniqueId.toString())
                        if(w == null){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${ld.second!!.name} 국가의 땅입니다."))
                        }
                        else if(!(w.countries == Pair(getInstance().getPlayer(e.player).country!!, ld.second!!) || w.countries == Pair(ld.second!!, getInstance().getPlayer(e.player).country!!))){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${ld.second!!.name} 국가의 땅입니다."))
                        }
                        else {
                            if(e.bucket.name.contains("LAVA")){
                                e.isCancelled = true
                                e.player.sendMessage(countryText("용암은 설치할 수 없습니다."))
                            }
                        }
                    }
                }
            }
        }
        else {
            val ld = getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z))

            if(getInstance().getPlayer(e.player).clan != null){
                if(ld != Pair<Clan?, Country?>(null, null)){
                    if(ld.first != null){
                        if(ld.first!!.owner.player.uniqueId != getInstance().getPlayer(e.player).clan!!.owner.player.uniqueId && !e.player.isOp){
                            e.isCancelled = true
                            e.player.sendMessage(clanText("이 땅은 ${ld.first!!.name} 클랜의 땅입니다."))
                        }
                    }
                    else if(ld.second != null){
                        if(ld.second != getInstance().getPlayer(e.player).country && !e.player.isOp){
                            val w = getInstance().getWar(ld.second!!.owner.player.uniqueId.toString())
                            if(w == null){
                                e.isCancelled = true
                                e.player.sendMessage(countryText("이 땅은 ${ld.second!!.name} 국가의 땅입니다."))
                            }
                            else if(!(w.countries == Pair(getInstance().getPlayer(e.player).country!!, ld.second!!) || w.countries == Pair(ld.second!!, getInstance().getPlayer(e.player).country!!))){
                                e.isCancelled = true
                                e.player.sendMessage(countryText("이 땅은 ${ld.second!!.name} 국가의 땅입니다."))
                            }
                        }
                    }
                }
            }
            else {
                if(ld != Pair<Clan?, Country?>(null, null)){
                    if(ld.first != null){
                        if(!e.player.isOp){
                            e.isCancelled = true
                            e.player.sendMessage(clanText("이 땅은 ${ld.first!!.name} 클랜의 땅입니다."))
                        }
                    }
                    else if(ld.second != null){
                        if(!e.player.isOp){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${ld.second!!.name} 국가의 땅입니다."))
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHangingPlace(e: HangingPlaceEvent) {
        if(e.player != null){
            if(getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z)) != Pair<Clan?, Country?>(null, null)){
                val c = getInstance().getLandOwned(Pair(e.block.chunk.x, e.block.chunk.z))
                if(c.first != null){
                    if(c.first != getInstance().getPlayer(e.player!!).clan && !e.player!!.isOp){
                        e.isCancelled = true
                        e.player!!.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                    }
                }
                else if(c.second != null){
                    if(getInstance().getPlayer(e.player!!).country != null){
                        if(c.second!!.owner.player.uniqueId != getInstance().getPlayer(e.player!!).country!!.owner.player.uniqueId && !e.player!!.isOp){
                            if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(
                                    e.player!!
                                ).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(
                                    e.player!!
                                ).country!!)){
                                e.isCancelled = true
                                e.player!!.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                            }
                        }
                    }
                    else {
                        e.isCancelled = true
                        e.player!!.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onHangingBreakByEntity(e: HangingBreakByEntityEvent) {
        if(e.remover is Player){
            if(getInstance().getLandOwned(Pair(e.entity.chunk.x, e.entity.chunk.z)) != Pair<Clan?, Country?>(null, null)){
                val c = getInstance().getLandOwned(Pair(e.entity.chunk.x, e.entity.chunk.z))
                if(c.first != null){
                    if(c.first != getInstance().getPlayer((e.remover as Player)).clan && !(e.remover as Player).isOp){
                        e.isCancelled = true
                        (e.remover as Player).sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                    }
                }
                else if(c.second != null){
                    if(getInstance().getPlayer((e.remover as Player)).country != null){
                        if(c.second!!.owner.player.uniqueId != getInstance().getPlayer((e.remover as Player)).country!!.owner.player.uniqueId && !(e.remover as Player).isOp){
                            if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(
                                    (e.remover as Player)
                                ).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(
                                    (e.remover as Player)
                                ).country!!)){
                                e.isCancelled = true
                                (e.remover as Player).sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                            }
                        }
                    }
                    else {
                        e.isCancelled = true
                        (e.remover as Player).sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) {
        if(getInstance().getLandOwned(Pair(e.rightClicked.chunk.x, e.rightClicked.chunk.z)) != Pair<Clan?, Country?>(null, null)){
            val c = getInstance().getLandOwned(Pair(e.rightClicked.chunk.x, e.rightClicked.chunk.z))
            if(c.first != null){
                if(c.first != getInstance().getPlayer(e.player).clan && !e.player.isOp){
                    e.isCancelled = true
                    e.player.sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                }
            }
            else if(c.second != null){
                if(getInstance().getPlayer(e.player).country != null){
                    if(c.second!!.owner.player.uniqueId != getInstance().getPlayer(e.player).country!!.owner.player.uniqueId && !e.player.isOp){
                        if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(
                                e.player
                            ).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(
                                e.player
                            ).country!!)){
                            e.isCancelled = true
                            e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                        }
                    }
                }
                else {
                    e.isCancelled = true
                    e.player.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        if(e.entity !is Player && e.damager is Player){
            if(getInstance().getLandOwned(Pair(e.entity.chunk.x, e.entity.chunk.z)) != Pair<Clan?, Country?>(null, null)){
                val c = getInstance().getLandOwned(Pair(e.entity.chunk.x, e.entity.chunk.z))
                if(c.first != null){
                    if(c.first != getInstance().getPlayer((e.damager as Player)).clan && !(e.damager as Player).isOp){
                        e.isCancelled = true
                        (e.damager as Player).sendMessage(clanText("이 땅은 ${c.first!!.name} 클랜의 땅입니다."))
                    }
                }
                else if(c.second != null){
                    if(getInstance().getPlayer((e.damager as Player)).country != null){
                        if(c.second!!.owner.player.uniqueId != getInstance().getPlayer((e.damager as Player)).country!!.owner.player.uniqueId && !(e.damager as Player).isOp){
                            if(getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(getInstance().getPlayer(
                                    (e.damager as Player)
                                ).country!!, c.second!!) && getInstance().getWar(c.second!!.owner.player.uniqueId.toString())?.countries != Pair(c.second!!, getInstance().getPlayer(
                                    (e.damager as Player)
                                ).country!!)){
                                e.isCancelled = true
                                (e.damager as Player).sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                            }
                        }
                    }
                    else {
                        e.isCancelled = true
                        e.damager.sendMessage(countryText("이 땅은 ${c.second!!.name} 국가의 땅입니다."))
                    }
                }
            }
        }
    }

    init {}
}