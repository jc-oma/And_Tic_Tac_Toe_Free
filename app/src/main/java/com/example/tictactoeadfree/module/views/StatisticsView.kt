package com.example.tictactoeadfree.module.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.example.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import kotlinx.android.synthetic.main.view_statistics.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class StatisticsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KoinComponent {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_statistics, this)
    }

    //TODO DatenbankService als Singelton implementieren
    private val viewModel: GameStatisticsViewModel by inject()

    fun getWonGamesForPlayer(wonPlayer: Int) {
        statistics_view_headline.text = if (wonPlayer == 1) {
            context.getString(R.string.statistics_view_o)
        } else {
            context.getString(R.string.statistics_view_x)
        }

        val gameStatistics: List<GameStatistics> = viewModel.getGameStatisticsList()
        val filteredGameStatisticForPlayer =
            gameStatistics.filter { it -> it.wonPlayer == wonPlayer }
        val wonGames = if (filteredGameStatisticForPlayer.count() == 0) {
            1
        } else {
            filteredGameStatisticForPlayer.count()
        }
        statistics_won_games.text = wonGames.toString()

        var averageTurns = 0
        filteredGameStatisticForPlayer
            .filter { it -> it.neededTurns != null }
            .forEach {
                averageTurns += it.neededTurns ?: 0
            }
        averageTurns /= wonGames
        statistics_needed_turns.text = averageTurns.toString()


        var averageTimeTaken: Long = 0
        filteredGameStatisticForPlayer
            .filter { it -> it.timeTakenToWin != null }
            .forEach {
                averageTimeTaken += it.timeTakenToWin ?: 0
            }
        averageTimeTaken /= wonGames
        //TODO in GameEngine berechnen lassen
        statistics_needed_time.text = averageTimeTaken.toString()
    }
}