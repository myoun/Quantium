package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.netherald.quantium.data.MiniGameData
import org.netherald.quantium.event.InstanceCreatedEvent
import org.netherald.quantium.event.MiniGameCreateEvent
import org.netherald.quantium.event.MiniGameDeletedEvent
import java.util.*
import kotlin.collections.ArrayList

class MiniGame(
    val owner : JavaPlugin,
    val name : String,
    val minPlayerSize : Int,
    val maxPlayerSize : Int,
    var maxInstanceSize : Int = 1,
    var defaultInstanceSize : Int = 1,
    private val instanceSettingValue : MiniGameInstance.() -> Unit,
) {

    val instances : List<MiniGameInstance> = ArrayList()
    val worldInstanceMap: HashMap<World, MiniGameInstance> = HashMap<World, MiniGameInstance>()
    val players : List<Player> = ArrayList()
    val queue : Queue<Player> = LinkedList()
    val unSafe = UnSafe()

    inner class UnSafe {
        fun init() {
            Bukkit.getPluginManager().callEvent(MiniGameCreateEvent(this@MiniGame))
            MiniGameInstance(this@MiniGame).apply(instanceSettingValue).unSafe.callMiniGameCreatedListener()

            for (i in 0 until defaultInstanceSize) { createInstance() }

            println("MiniGame $name is loaded")
        }
    }

    val worlds : Collection<World> get() = worldInstanceMap.keys

    fun pollQueuePlayers(count : Int = 1) : Collection<Player> {
        val returnSize = if (count < queue.size) count else queue.size
        val out = ArrayList<Player>( returnSize )
        for (i in 0 until returnSize) {
            queue.poll()?.let { player ->
                out.add(player)
            } ?: return out
        }
        return out
    }

    fun addPlayer(player: Player) {
        recommendMatchingInstance?.addPlayer(player) ?: run { queue.add(player) }
    }

    val recommendMatchingInstance : MiniGameInstance?
        get() {
            var out : MiniGameInstance? = null
            for (instance in instances) {
                if (!instance.isStarted && !instance.isFinished) {
                    out?.let {
                        if (it.players.size < instance.players.size) {
                            out = instance
                        }
                    } ?: run { out = instance }
                }
            }
            return out
        }

    val matchingInstances : List<MiniGameInstance>
        get() {
            val out = ArrayList<MiniGameInstance>()
            for (instance in instances) {
                if (!(instance.isStarted && instance.isFinished)) {
                    out.add(instance)
                }
            }
            return out
        }

    fun createInstance() {

        val instance = MiniGameInstance(
            this,
        ).apply(instanceSettingValue)

        val clone = fun (world : World, name : String) = instance.worldSetting.worldEditor.cloneWorld(world, name)
        val addWorld = fun (base : World, addWorldType : MiniGameInstance.AddWorldType) =
            instance.addWorld(clone(base, "${instance.uuid}_${base.name}"), addWorldType)

        instance.worldSetting.baseWorld?.let { addWorld(it, MiniGameInstance.AddWorldType.NORMAL) }
        instance.worldSetting.baseWorldNether?.let { addWorld(it, MiniGameInstance.AddWorldType.NETHER) }
        instance.worldSetting.baseWorldEnder?.let { addWorld(it, MiniGameInstance.AddWorldType.ENDER) }
        instance.worldSetting.otherBaseWorlds.forEach { addWorld(it, MiniGameInstance.AddWorldType.OTHER) }

        addInstance(instance)

        instance.unSafe.callInstanceCreatedListener()

        Bukkit.getServer().pluginManager.callEvent(InstanceCreatedEvent(instance))

        if (instance.automaticFunctionSetting.autoPlayerPool) {
            pollQueuePlayers(maxPlayerSize - instance.players.size).forEach { player ->
                instance.addPlayer(player)
            }
        }

        println("$name's instance is added")
    }

    fun stopAll() {
        defaultInstanceSize = 0
        instances.forEach { instance ->
            instance.stopGame()
        }
    }

    fun delete() {

        stopAll()
        MiniGameData.miniGames.remove(name, this)
        Bukkit.getPluginManager().callEvent(MiniGameDeletedEvent(this))

    }

    private fun addInstance(instance: MiniGameInstance) {
        instances as ArrayList
        instances.add(instance)
        MiniGameData.instances[instance.uuid] = instance
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MiniGame

        if (name != other.name) return false

        return true
    }

    override fun toString(): String { return name }

    override fun hashCode(): Int { return name.hashCode() }
}
