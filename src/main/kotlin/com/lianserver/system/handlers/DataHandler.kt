package com.lianserver.system.handlers

import com.lianserver.system.common.Clan
import com.lianserver.system.common.Country
import com.lianserver.system.common.LianPlayer
import com.lianserver.system.plugin.LianPlugin
import java.io.File
import java.util.*
import java.util.logging.Level

object DataHandler {
    fun getInstance() = LianPlugin.instance
    private val logger = getInstance().logger
    
    fun saveToFile(){
        val clanDir = File("plugins/LianMain/clans")
        clanDir.deleteRecursively()

        clanDir.mkdir()
        getInstance().clans.forEach {
            getInstance().logger.info(it.value.toString())
            File("plugins/LianMain/clans/${it.value.owner.player.uniqueId}.txt").writeText(it.toString())
        }

        val countryDir = File("plugins/LianMain/countries")
        countryDir.deleteRecursively()

        countryDir.mkdir()
        getInstance().countries.forEach {
            getInstance().logger.info(it.value.toString())
            File("plugins/LianMain/countries/${it.value.owner.player.uniqueId}.txt").writeText(it.toString())
        }

        val playerDir = File("plugins/LianMain/players")
        playerDir.deleteRecursively()

        playerDir.mkdir()
        getInstance().onlinePlayers.forEach {
            getInstance().logger.info(it.value.toString())
            File("plugins/LianMain/players/${it.value.player.uniqueId}.txt").writeText(it.value.toString())
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
                        val f = file.readText().split("\n")
                        val c = LianPlayer(getInstance().server.getOfflinePlayer(UUID.fromString(f[0].split("=")[0])))
                        logger.info(file.readText())

                        c.clan = if (getInstance().clans.any { it.value.owner.player.uniqueId.toString() == f[1] }) {
                            getInstance().clans[f[1]]
                        }
                        else null

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
                    val c = file.readText().split("\n")

                    logger.info(c.size.toString())
                    getInstance().clans[c[0].split("=")[0].trim()] = Clan(
                        o = getInstance().onlinePlayers[c[0].trim().split("=")[0]]!!,
                        l = if (c[1] != "null"){
                            Pair<Int, Int>(c[1].split(", ")[0].toInt(), c[1].split(", ")[1].toInt())
                        } else {
                               null
                        },
                        p = c.subList(3, c.size).map { el ->
                            logger.info(el)
                            getInstance().onlinePlayers[el.trim()]!!
                        } as MutableList<LianPlayer>,
                        n = c[1]
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
                    val c = file.readText().split("\n")

                    logger.info(c.size.toString())
                    getInstance().countries[c[0].split("=")[0].trim()] = Country(
                        getInstance().onlinePlayers[c[0].trim().split("=")[0]]!!,
                        l = if (c[1] != "null"){
                            Pair<Int, Int>(c[1].split(", ")[0].toInt(), c[1].split(", ")[1].toInt())
                        } else {
                            null
                        },
                        c.subList(4, c.size).map { el ->
                            logger.info(el)
                            getInstance().onlinePlayers[el.trim()]!!
                        } as MutableList<LianPlayer>,
                        n = c[2],
                        d = c[3].toInt()
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
                    if(getInstance().onlinePlayers.containsKey(file.readText().split("\n")[0].trim())){
                        val f = file.readText().split("\n")
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
                    }
                }
            }
        }
    }
}