package com.jcDevelopment.tictactoeadfree.module.statistics

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
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

    private val viewModel: GameStatisticsViewModel by inject()

    fun getWonGamesForPlayer(
        wonPlayer: Int,
        drawableAsHeadline: Drawable?,
        gameMode: GameMode
    ) {
        statistics_view_headline.setImageDrawable(drawableAsHeadline)

        val gameStatistics: List<GameStatistics> = viewModel.getGameStatisticsList()
        val filteredGameStatisticForPlayer =
            gameStatistics.filter { it -> it.wonPlayer == wonPlayer && it.gameMode == gameMode.toString() }
        val wonGames = filteredGameStatisticForPlayer.count()
        statistics_won_games.text = wonGames.toString()

        var averageTurns = 0
        filteredGameStatisticForPlayer
            .filter { it -> it.neededTurns != null && it.gameMode == gameMode.toString()}
            .forEach {
                averageTurns += it.neededTurns ?: 0
            }
        averageTurns = if (wonGames == 0) 0 else {
            averageTurns / wonGames
        }
        statistics_needed_turns.text = averageTurns.toString()


        var averageTimeTaken: Long = 0

        filteredGameStatisticForPlayer
            .filter { it -> it.timeTakenToWin != null }
            .forEach {
                averageTimeTaken += it.timeTakenToWin ?: 0
            }
        averageTimeTaken = if (wonGames == 0) 0 else {
            averageTimeTaken / wonGames
        }

        statistics_needed_time.text = averageTimeTaken.toString()
    }
}