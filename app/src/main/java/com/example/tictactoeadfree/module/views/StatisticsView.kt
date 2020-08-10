package com.example.tictactoeadfree.module.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.room.Room
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.data.AppDatabase
import com.example.tictactoeadfree.module.data.GameStatistics
import com.example.tictactoeadfree.module.data.GameStatisticsRepository
import com.example.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import kotlinx.android.synthetic.main.view_statistics.view.*

class StatisticsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_statistics, this)
    }

    //TODO DatenbankService als Singelton implementieren (KOIN)
    private lateinit var viewModel: GameStatisticsViewModel

    private val dbName = "TIC_TAC_TOE_GAME_STATISTICS_DB"

    //Fixme not on main thread
    private val appDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, dbName).allowMainThreadQueries()
            .build()

    private val gameRepo = GameStatisticsRepository.getInstance(appDatabase.gameStatisticsDao())

    override fun onFinishInflate() {
        super.onFinishInflate()

        viewModel = GameStatisticsViewModel(gameRepo)
    }

    fun getWonGamesForPlayer(wonPlayer: Int) {
        statistics_view_headline.text = if (wonPlayer == 1) {
            context.getString(R.string.statistics_view_x)
        } else {
            context.getString(R.string.statistics_view_o)
        }

        val gameStatistics: List<GameStatistics> = viewModel.getGameStatisticsList()
        val filteredGameStatisticForPlayer =
            gameStatistics.filter { it -> it.wonPlayer == wonPlayer }
        val wonGames = if (filteredGameStatisticForPlayer.count() == 0) { 1 } else { filteredGameStatisticForPlayer.count() }
        statistics_won_games.text = wonGames.toString()

        var averageTurns: Int = 0
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