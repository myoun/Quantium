package org.netherald.quantium

object RedisMessageType {

    const val MINI_GAME_ADDED = "added-mini-game"
    const val MINI_GAME_REMOVED = "removed-mini-game"

    const val ADDED_INSTANCE = "added-instance"
    const val DELETED_INSTANCE = "deleted-instance"

    const val STARTED_INSTANCE = "started-instance"
    const val STOPPED_INSTANCE = "stopped-instance"

    const val BLOCK = "block"

    const val REJOIN_DATA_ADD = "rejoin-data-add"
    const val REJOIN_DATA_REMOVE = "rejoin-data-remove"
}