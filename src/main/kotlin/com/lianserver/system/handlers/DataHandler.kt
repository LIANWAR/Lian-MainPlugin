package com.lianserver.system.handlers

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lianserver.system.common.*
import com.lianserver.system.plugin.LianPlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level

object DataHandler {
    fun getInstance() = LianPlugin.instance
    private val logger = getInstance().logger
    
    fun saveToFile(){
        val d: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        File("plugins/LianMain/backups").mkdir()
        File("plugins/LianMain/backups/${d}").mkdir()

        val clanDir = File("plugins/LianMain/clans")
        clanDir.renameTo(File("plugins/LianMain/backups/${d}/clans"))

        clanDir.mkdir()
        getInstance().clans.forEach {
            getInstance().logger.info(it.value.toString())
            it.value.serialize().save(File("plugins/LianMain/clans/${it.value.owner.player.uniqueId}.yml"))
        }

        val countryDir = File("plugins/LianMain/countries")
        countryDir.renameTo(File("plugins/LianMain/backups/${d}/countries"))

        countryDir.mkdir()
        getInstance().countries.forEach {
            getInstance().logger.info(it.value.toString())
            it.value.serialize().save(File("plugins/LianMain/countries/${it.value.owner.player.uniqueId}.yml"))
        }

        val playerDir = File("plugins/LianMain/players")
        playerDir.renameTo(File("plugins/LianMain/backups/${d}/players"))

        playerDir.mkdir()
        getInstance().onlinePlayers.forEach {
            getInstance().logger.info(it.value.toString())
            it.value.serialize().save(File("plugins/LianMain/players/${it.value.player.uniqueId}.yml"))
        }

        val war = File("plugins/LianMain/wars")
        war.renameTo(File("plugins/LianMain/backups/${d}/wars"))

        war.mkdir()
        getInstance().wars.forEach {
            getInstance().logger.info(it.toString())
            it.serialize().save(File("plugins/LianMain/wars/${it.countries.first.owner.player.uniqueId}.yml"))
        }

        File("plugins/LianMain/backups/${d}/shop").mkdir()

        val shopc = File("plugins/LianMain/shop/cshop")
        shopc.renameTo(File("plugins/LianMain/backups/${d}/shop/cshop"))
        shopc.mkdir()

        val shopu = File("plugins/LianMain/shop/ushop")
        shopu.renameTo(File("plugins/LianMain/backups/${d}/shop/ushop"))
        shopu.mkdir()

        getInstance().cashShopItem.forEach {
            it.save(File("plugins/LianMain/shop/cshop/${it.hashCode()}.yml"))
        }
        getInstance().userShopItem.forEach {
            it.save(File("plugins/LianMain/shop/ushop/${it.hashCode()}.yml"))
        }

        val outpost = File("plugins/LianMain/outposts")
        outpost.renameTo(File("plugins/LianMain/backups/${d}/outposts"))

        outpost.mkdir()
        getInstance().outpostData.values.forEach {
            getInstance().logger.info(it.toString())
            it.serialize().save(File("plugins/LianMain/outposts/${it.pos.hashCode()}.yml"))
        }

        val ancient = File("plugins/LianMain/ancients")
        ancient.renameTo(File("plugins/LianMain/backups/${d}/ancients"))

        ancient.mkdir()
        getInstance().ancientsData.values.forEach {
            getInstance().logger.info(it.toString())
            it.serialize().save(File("plugins/LianMain/ancients/${it.pos.hashCode()}.yml"))
        }
    }
    
    fun loadFromFile(){
        val testPluginDir = File("plugins/LianMain")
        if(!testPluginDir.exists()){
            testPluginDir.mkdir()
        }

        var playerDir = File("plugins/LianMain/players")
        if(!(playerDir.exists() && playerDir.isDirectory)){
            logger.warning("플레이어 저장 경로가 없거나 폴더가 아닙니다.")

            if(!playerDir.isDirectory){
                logger.log(Level.OFF, "플레이어 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                playerDir.mkdir()
            }
        }
        else {
            playerDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info(file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    val cl = LianPlayer.deserialize(c)
                    getInstance().onlinePlayers[cl.player.uniqueId.toString()] = cl
                }
            }
        }

        val clanDir = File("plugins/LianMain/clans")
        if(!(clanDir.exists() && clanDir.isDirectory)){
            logger.warning("클랜 저장 경로가 없거나 폴더가 아닙니다.")

            if(!clanDir.isDirectory){
                logger.log(Level.OFF, "클랜 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                clanDir.mkdir()
            }
        }
        else {
            clanDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info(file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    val cl = Clan.deserialize(c)
                    getInstance().clans[cl.owner.player.uniqueId.toString()] = cl
                }
            }
        }

        val countryDir = File("plugins/LianMain/countries")
        if(!(countryDir.exists() && countryDir.isDirectory)){
            logger.warning("클랜 저장 경로가 없거나 폴더가 아닙니다.")

            if(!countryDir.isDirectory){
                logger.log(Level.OFF, "클랜 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                clanDir.mkdir()
            }
        }
        else {
            countryDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info(file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    val cl = Country.deserialize(c)
                    getInstance().countries[cl.owner.player.uniqueId.toString()] = cl
                }
            }
        }

        playerDir = File("plugins/LianMain/players")
        if(!(playerDir.exists() && playerDir.isDirectory)){
            logger.warning("플레이어 저장 경로가 없거나 폴더가 아닙니다.")

            if(!playerDir.isDirectory){
                logger.log(Level.OFF, "플레이어 저장 경로와 같은 이름의 파일이 있습니다.")
                
            }
            else {
                playerDir.mkdir()
            }
        }
        else {
            playerDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info("postprocessing " + file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    if(c.getString("clan") != "null"){
                        getInstance().onlinePlayers[getInstance().server.getOfflinePlayer(UUID.fromString(c.getString("player"))).uniqueId.toString()]!!.clan = getInstance().clans[c.getString("clan")]!!
                    }
                    if(c.getString("country") != "null"){
                        getInstance().onlinePlayers[getInstance().server.getOfflinePlayer(UUID.fromString(c.getString("player"))).uniqueId.toString()]!!.country = getInstance().countries[c.getString("country")]!!
                    }

                    getInstance().onlinePlayers[
                            getInstance().server.getOfflinePlayer(
                                UUID.fromString(
                                    c.getString("player")
                                )
                            ).uniqueId.toString()
                    ]!!.let {
                        it.cash = c.getInt("cash")
                        it.lastCCDay = c.getString("lcd") ?: "19890604"
                        it.ccStreak = c.getInt("ccs")
                        it.prefix = c.getString("prefix") ?: ""
                    }
                }
            }
        }
        
        val warDir = File("plugins/LianMain/wars")
        if(!(warDir.exists() && warDir.isDirectory)){
            logger.warning("전쟁 저장 경로가 없거나 폴더가 아닙니다.")

            if(!warDir.isDirectory){
                logger.log(Level.OFF, "전쟁 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                warDir.mkdir()
            }
        }
        else {
            warDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info(file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    getInstance().wars.add(War.deserialize(c))
                }
            }
        }
        getInstance().cashShopItem = mutableListOf()
        File("plugins/LianMain/shop/cshop").listFiles()?.forEach{
            getInstance().cashShopItem.add(
                YamlConfiguration.loadConfiguration(it)
            )
        }
        getInstance().userShopItem = mutableListOf()
        File("plugins/LianMain/shop/ushop").listFiles()?.forEach{
            getInstance().userShopItem.add(
                YamlConfiguration.loadConfiguration(it)
            )
        }

        val outpostDir = File("plugins/LianMain/outposts")
        if(!(outpostDir.exists() && outpostDir.isDirectory)){
            logger.warning("전쟁 저장 경로가 없거나 폴더가 아닙니다.")

            if(!outpostDir.isDirectory){
                logger.log(Level.OFF, "전쟁 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                outpostDir.mkdir()
            }
        }
        else {
            outpostDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info(file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    val cl = Outpost.deserialize(c)
                    getInstance().outpostData[cl.pos.hashCode().toString()] = cl
                }
            }
        }

        val ancientDir = File("plugins/LianMain/ancients")
        if(!(ancientDir.exists() && ancientDir.isDirectory)){
            logger.warning("전쟁 저장 경로가 없거나 폴더가 아닙니다.")

            if(!ancientDir.isDirectory){
                logger.log(Level.OFF, "전쟁 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                ancientDir.mkdir()
            }
        }
        else {
            ancientDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".yml")){
                    logger.info(file.readText())
                    val c = YamlConfiguration.loadConfiguration(file)
                    val cl = Ancients.deserialize(c)
                    getInstance().ancientsData[cl.pos.hashCode().toString()] = cl
                }
            }
        }
    }
}