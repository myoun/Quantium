package org.netherald.quantium

import org.bukkit.entity.Player

abstract class TeamMatcher {
    abstract fun match(players : List<Player>) : List<List<Player>>
}