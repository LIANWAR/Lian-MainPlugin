package com.underconnor.lian.handlers

import com.underconnor.lian.common.Clan
import com.underconnor.lian.common.Land
import com.underconnor.lian.common.LianPlayer
import com.underconnor.lian.plugin.LianPlugin
import java.io.File
import java.util.*
import java.util.logging.Level

object DataHandler {
    fun getInstance() = LianPlugin.instance
    private val logger = getInstance().logger
    
    fun saveToFile(){
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
            getInstance().clans.forEach {
                File("plugins/LianMain/clans/${it.value.owner.player.uniqueId}.txt").writeText(it.toString())
            }
        }

        val playerDir = File("plugins/LianMain/clans")
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
            getInstance().onlinePlayers.forEach {
                File("plugins/LianMain/players/${it.value.player.uniqueId}.txt").writeText(it.value.toString())
            }
        }

        val landDir = File("plugins/LianMain/lands")
        if(!(landDir.exists() && landDir.isDirectory)){
            logger.warning("땅 저장 경로가 없거나 폴더가 아닙니다.")

            if(!landDir.isDirectory){
                logger.log(Level.OFF, "땅 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                landDir.mkdir()
            }
        }
        else {
            getInstance().lands.forEach {
                File("plugins/LianMain/lands/${it.key}.txt").writeText(it.value.toString())
            }
        }
    }
    
    fun loadFromFile(){
        val testPluginDir = File("plugins/LianMain")
        if(!testPluginDir.exists()){
            testPluginDir.mkdir()
        }

        var playerDir = File("plugins/LianMain/clans")
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
                        val c = LianPlayer(getInstance().server.getOfflinePlayer(UUID.fromString(f[0])))
                        logger.info(getInstance().clans.toString())

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
                    val c = file.readText().split("\n")

                    logger.info(c.size.toString())
                    getInstance().clans[c[0].trim()] = Clan(
                        getInstance().onlinePlayers[c[0].trim()]!!,
                        c.subList(2, c.size).map { el ->
                            logger.info(el)
                            getInstance().onlinePlayers[el.trim()]!!
                        } as MutableList<LianPlayer>,
                        n = c[1]
                    )
                }
            }
        }

        val landDir = File("plugins/LianMain/lands")
        if(!(landDir.exists() && landDir.isDirectory)){
            logger.warning("땅 저장 경로가 없거나 폴더가 아닙니다.")

            if(!landDir.isDirectory){
                logger.log(Level.OFF, "땅 저장 경로와 같은 이름의 파일이 있습니다.")
            }
            else {
                landDir.mkdir()
            }
        }
        else {
            landDir.listFiles()?.forEach { file ->
                if(file.name.endsWith(".txt")){
                    val c = file.readText().split("\n")

                    logger.info(c.size.toString())
                    getInstance().lands[Pair(c[0].split(" ")[0].toInt(), c[0].split(" ")[1].toInt())] = Land(
                        oo = getInstance().onlinePlayers[c[1]]!!,
                        o = c.subList(2, c.size).map {
                            getInstance().onlinePlayers[it]!!
                        } as MutableList<LianPlayer>,
                        pos = Pair(c[0].split(" ")[0].toInt(), c[0].split(" ")[1].toInt())
                    )
                }
            }
        }

        playerDir = File("plugins/LianMain/clans")
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
                    if(getInstance().onlinePlayers.containsKey(file.readText().split("\n")[0].trim())){
                        val f = file.readText().split("\n")
                        if(f[1].trim() != (getInstance().onlinePlayers[f[0].trim()]!!.clan?.owner?.player?.uniqueId.toString())){
                            getInstance().onlinePlayers[f[0].trim()]!!.clan = getInstance().clans[f[1].trim()]
                        }
                    }
                }
            }
        }
    }
}