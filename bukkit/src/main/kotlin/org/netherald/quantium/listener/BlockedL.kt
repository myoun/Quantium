package org.netherald.quantium.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.netherald.quantium.RedisKeyType
import org.netherald.quantium.RedisMessageType
import event.BlockedServerEvent
import org.netherald.quantium.util.RedisServerUtil

class BlockedL : Listener {

    private fun redisPatch(value : Boolean) {
        RedisServerUtil.instance?.let {
            RedisServerUtil.client!!.connectPubSub().sync().publish(
                "${RedisKeyType.SERVER}:${it.serverName}:${RedisMessageType.BLOCK}",
                value.toString()
            )
        }
    }

    @EventHandler
    fun onBlock(event : BlockedServerEvent) {
        RedisServerUtil.instance?.let {
            redisPatch(true)
        }
    }

    @EventHandler
    fun onUnBlock(event : BlockedServerEvent) {
        RedisServerUtil.instance?.let {
            redisPatch(false)
        }
    }
}