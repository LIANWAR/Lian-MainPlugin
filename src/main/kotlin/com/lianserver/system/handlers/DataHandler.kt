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
            File("plugins/LianMain/clans/${it.value.owner.player.uniqueId}.txt").writeText(it.toString())
        }

        val countryDir = File("plugins/LianMain/countries")
        countryDir.renameTo(File("plugins/LianMain/backups/${d}/countries"))

        countryDir.mkdir()
        getInstance().countries.forEach {
            getInstance().logger.info(it.value.toString())
            File("plugins/LianMain/countries/${it.value.owner.player.uniqueId}.txt").writeText(it.toString())
        }

        val playerDir = File("plugins/LianMain/players")
        playerDir.renameTo(File("plugins/LianMain/backups/${d}/players"))

        playerDir.mkdir()
        getInstance().onlinePlayers.forEach {
            getInstance().logger.info(it.value.toString())
            File("plugins/LianMain/players/${it.value.player.uniqueId}.txt").writeText(it.value.toString())
        }

        val war = File("plugins/LianMain/wars")
        war.renameTo(File("plugins/LianMain/backups/${d}/wars"))

        war.mkdir()
        getInstance().wars.forEach {
            getInstance().logger.info(it.toString())
            File("plugins/LianMain/wars/${it.countries.first.owner.player.uniqueId}.txt").writeText(it.toString())
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
                if(file.name.endsWith(".txt")){
                    if(getInstance().onlinePlayers.none { it.value.player.uniqueId.toString() == file.readText().split("\n")[0] }){
                        val f = file.readText().split("\n").map { it.trim() }
                        val c = LianPlayer(getInstance().server.getOfflinePlayer(UUID.fromString(f[0].split("=")[0])))
                        logger.info(file.readText())

                        c.clan = if (getInstance().clans.any { it.value.owner.player.uniqueId.toString() == f[1] }) {
                            getInstance().clans[f[1]]
                        }
                        else null

                        c.country = if (getInstance().countries.any { it.value.owner.player.uniqueId.toString() == f[2] }) {
                            getInstance().countries[f[2]]
                        }
                        else null

                        c.prefix = f[3]
                        c.lastCCDay = f[4]
                        c.cash = f[5].toInt()
                        c.ccStreak = f[6].toInt()

                        getInstance().onlinePlayers[c.player.uniqueId.toString()] = c
                    }
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
                if(file.name.endsWith(".txt")){
                    logger.info(file.readText())
                    val c = file.readText().split("\n").map { it.trim() }.toMutableList()

                    logger.info(c.size.toString())
                    getInstance().clans[c[0].split("=")[0].trim()] = Clan(
                        o = getInstance().onlinePlayers[c[0].trim().split("=")[0]]!!,
                        l = if (c[1] != "null"){
                            c[1] = c[1].replace("(", "").replace(")", "")
                            Pair(c[1].split(", ")[0].toInt(), c[1].split(", ")[1].toInt())
                        } else {
                               null
                        },
                        p = c.subList(4, c.size).map { el ->
                            logger.info(el)
                            getInstance().onlinePlayers[el.trim()]!!
                        } as MutableList<LianPlayer>,
                        n = c[2],
                        ip = c[3] == "true"
                    )
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
                if(file.name.endsWith(".txt")){
                    logger.info(file.readText())
                    val c = file.readText().split("\n").map { it.trim() }.toMutableList()

                    logger.info(c.size.toString())
                    getInstance().countries[c[0].split("=")[0].trim()] = Country(
                        getInstance().onlinePlayers[c[0].trim().split("=")[0]]!!,
                        land = if (c[1] != "null"){
                            c[1] = c[1].replace("(", "").replace(")", "")
                            Pair(c[1].split(", ")[0].toInt(), c[1].split(", ")[1].toInt())
                        } else {
                            null
                        },
                        players = c.subList(7, c.size).map { el ->
                            logger.info(el)
                            getInstance().onlinePlayers[el.trim()]!!
                        } as MutableList<LianPlayer>,
                        name = c[2],
                        warDeclarationDenyCount = c[5].toInt(),
                        lastWarDeclaratedTime = Date(c[6].toLong()),
                        public = c[3] == "true",
                        winCount = c[4].toInt()
                    )
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
                if(file.name.endsWith(".txt")){
                    logger.info("postprocessing " + file.readText())
                    if(getInstance().onlinePlayers.containsKey(file.readText().split("\n").map { it.trim() }[0].trim())){
                        val f = file.readText().split("\n").map { it.trim() }
                        if(f[1] == "null"){
                            if(f[2] != "null"){
                                getInstance().onlinePlayers[f[0].trim()]!!.country = getInstance().countries[f[2].trim()]
                                getInstance().onlinePlayers[f[0].trim()]!!.clan = null
                            }
                            else {
                                getInstance().onlinePlayers[f[0].trim()]!!.clan = getInstance().clans[f[1].trim()]
                                getInstance().onlinePlayers[f[0].trim()]!!.country = null
                            }
                        }
                        else {
                            getInstance().onlinePlayers[f[0].trim()]!!.clan = getInstance().clans[f[1].trim()]
                            getInstance().onlinePlayers[f[0].trim()]!!.country = null
                        }
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
                if(file.name.endsWith(".txt")){
                    val c = file.readText().split("\n")
                    
                    getInstance().wars.add(
                        War(
                            Date(c[0].toLong()),
                            Pair(getInstance().countries[c[1].split(" ")[0]]!!, getInstance().countries[c[1].split(" ")[1]]!!)
                        )
                    )
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
    }
}