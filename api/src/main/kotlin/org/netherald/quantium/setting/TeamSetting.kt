package org.netherald.quantium.setting

import org.bukkit.entity.Player
import org.netherald.quantium.DefaultTeamMatcher
import org.netherald.quantium.TeamMatcher

data class TeamSetting(
    var enable: Boolean = false,
    var teamSize: Int = 0,
) {
    var teamMatcher: TeamMatcher = DefaultTeamMatcher(this)

    fun teamMatcher(code: (Collection<Player>) -> List<List<Player>>) {
        teamMatcher = object : TeamMatcher {
            override fun match(players: Collection<Player>): List<List<Player>> {
                return code(players)
            }
        }
    }

}