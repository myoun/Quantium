package org.netherald.quantium

import org.bukkit.entity.Player
import org.netherald.quantium.setting.TeamSetting
import kotlin.collections.ArrayList

class DefaultTeamMatcher(private val teamGameSetting: TeamSetting) : TeamMatcher {
    override fun match(players: Collection<Player>): List<List<Player>> {
        val playersData = players.shuffled()
        val out = ArrayList<ArrayList<Player>>(teamGameSetting.teamSize).apply {
            for (i in 0 until size) { this[i] = ArrayList() }
            var index = 0
            playersData.forEach {
                if (index == size) index = 0
                this[index].add(it)
                index++
            }
        }
        return out
    }
}