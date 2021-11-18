package org.netherald.quantium

import org.bukkit.Bukkit
import org.bukkit.scoreboard.Score

class QuantiumBoard(name : String, displayName : String) {

    private val scores = HashMap<Int, ArrayList<Score>>()
    private val manager = Bukkit.getScoreboardManager()!!
    val board = manager.newScoreboard
    var objective = board.registerNewObjective(name, "dummy", displayName)

    infix fun String.to(value : Int) : Score {
        val score: Score = objective.getScore(this)
        score.score = value
        scores[value] ?: run { scores[value] = ArrayList() }
        scores[value]!! += score
        return score
    }

    infix fun Int.to(value : String) : Score {
        val score: Score = objective.getScore(value)
        score.score = this
        scores[this]?.forEach { board.resetScores(it.entry) }
        scores[this] = ArrayList()
        scores[this]!! += score
        return score
    }

    var displayName : String
    get() = objective.displayName
    set(value) { objective.displayName = value }

}