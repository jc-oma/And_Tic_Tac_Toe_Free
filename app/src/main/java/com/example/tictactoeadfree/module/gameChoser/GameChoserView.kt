package com.example.tictactoeadfree.module.gameChoser

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.data.gameSettings.GameMode
import com.example.tictactoeadfree.module.data.gameSettings.GameSettings
import com.example.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import kotlinx.android.synthetic.main.view_game_choser.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class GameChoserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KoinComponent {

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    init {
        initView(context)
        initClickListener()
        initLastChosenGame()
    }

    private fun initLastChosenGame() {
        val gameMode = if (gameSettingsViewModel.getGameSettings().isEmpty()) {
            GameSettings().gameMode
        } else {
            gameSettingsViewModel.getGameSettings().last().gameMode
        }
        presentChosenGame(GameMode.valueOf(gameMode))
    }

    private fun initClickListener() {
        game_choser_game_one.setOnClickListener {
            choseGame(GameMode.TIC_TAC_TOE)
        }

        game_choser_game_two.setOnClickListener {
            choseGame(GameMode.FOUR_IN_A_ROW)
        }
    }

    private fun choseGame(gameMode: GameMode) {
        val lastSettings = if (gameSettingsViewModel.getGameSettings().isEmpty()) {
            GameSettings()
        } else {
            gameSettingsViewModel.getGameSettings().last()
        }
        gameSettingsViewModel.createGameSettings(
            GameSettings(
                lastSettings.isSecondPlayerAi,
                gameMode.toString()
            )
        )
        presentChosenGame(gameMode)
    }

    private fun presentChosenGame(gameMode: GameMode) {
        game_choser_game_two_image_view.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                when (gameMode) {
                    GameMode.TIC_TAC_TOE -> R.drawable.spooky_fourinarowchooser_unchosen
                    GameMode.FOUR_IN_A_ROW -> R.drawable.spooky_fourinarowchooser
                }
            )
        )

        game_choser_game_one_image_view.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                when (gameMode) {
                    GameMode.TIC_TAC_TOE -> R.drawable.spooky_tictactoe_game_chooser
                    GameMode.FOUR_IN_A_ROW -> R.drawable.spooky_tictactoe_game_chooser_unchosen
                }
            )
        )
    }


    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_game_choser, this)
    }
}