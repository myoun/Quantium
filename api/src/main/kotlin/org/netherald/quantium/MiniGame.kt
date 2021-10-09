package org.netherald.quantium

import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.dataclass.TeamSetting
import org.netherald.quantium.dataclass.WorldSetting
import org.netherald.quantium.world.WorldEditor
import java.util.*
import kotlin.collections.ArrayList

data class MiniGame(
    val owner : JavaPlugin,
    val name : String,
    val minPlayerSize : Int,
    val maxPlayerSize : Int,
    val enableRejoin : Boolean,
    val perPlayerList : Boolean,
    var maxInstanceSize : Int,
    var defaultInstanceSize : Int,
    val teamSetting : TeamSetting,
    val worldSetting: WorldSetting,
    val instanceSettingValue : MiniGameInstance.() -> Unit,
    val miniGameInstances : ArrayList<MiniGameInstance> = ArrayList(),
    val worldInstanceMap: HashMap<String, MiniGameInstance> = HashMap<String, MiniGameInstance>()
) {
    init {

        MiniGameInstance(
            this,
            ArrayList()
        ).apply(instanceSettingValue).unsafe().callMiniGameCreatedListener()

        for (i in 0 until defaultInstanceSize) {
            createInstance()
        }

    }

    val worlds : Collection<String> = worldInstanceMap.keys

    fun createInstance() {
        val worldNameId = UUID.randomUUID().toString()
        val worlds = ArrayList<String>()

        worldSetting.baseWorld?.let { worlds.add(worldNameId+it) }
        worldSetting.baseWorldNether?.let { worlds.add(worldNameId+it) }
        worldSetting.baseWorldTheNether?.let { worlds.add(worldNameId+it) }
        worldSetting.otherWorlds.forEach { worlds.add(worldNameId+it) }

        val instance = MiniGameInstance(
            this,
            worlds
        ).apply(instanceSettingValue)

        miniGameInstances.add(instance)
        worlds.forEach { worldInstanceMap[it] = instance }
    }

    fun deleteInstance(instance : MiniGameInstance) {
        instance.stopGame()
        removeInstanceInData(instance)
    }

    fun removeInstanceInData(instance : MiniGameInstance) {
        miniGameInstances.remove(instance)
        worlds.forEach { worldInstanceMap.remove(it) }
    }

    fun stopAll() {
        miniGameInstances.forEach {
            it.stopGame()
        }
        worlds.forEach { WorldEditor.worldEditor.deleteWorld(it) }
    }
}
