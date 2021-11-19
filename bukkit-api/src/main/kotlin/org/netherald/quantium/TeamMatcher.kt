package org.netherald.quantium

import org.bukkit.entity.Player

interface TeamMatcher {
    fun match(players : Collection<Player>) : List<List<Player>>
}