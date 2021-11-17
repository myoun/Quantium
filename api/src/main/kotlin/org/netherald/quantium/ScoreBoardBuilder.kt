package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.scoreboard.Score

class ScoreBoardBuilder(name : String, displayName : String) {

    private val scores : Collection<Score> = HashSet()
    private val manager = Bukkit.getScoreboardManager()!!
    val board = manager.newScoreboard
    var objective = board.registerNewObjective(name, "dummy", displayName)

    infix fun String.to(value : Int) : Score {
        val score: Score = objective.getScore(this)
        score.score = value
        (scores as MutableCollection<Score>).add(score)
        return score
    }
}