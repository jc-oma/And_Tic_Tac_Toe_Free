package com.jcDevelopment.tictactoeadfree.module.gameChoser

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import kotlinx.android.synthetic.main.view_game_choser.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class GameChoserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), KoinComponent {

    private val loadAnimation = AnimationUtils.loadAnimation(
        context,
        R.anim.choser_check_appear
    )

    private val toBeHardwareAcceleratedViews by lazy { listOf<View>(
        game_choser_game_one_check,
        game_choser_game_two_check
    ) }

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    init {
        initView(context)
        initClickListener()
        initLastChosenGame()
        initHardwareAcceleration()
    }

    private fun initHardwareAcceleration() {
        toBeHardwareAcceleratedViews.forEach {
            it.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
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
        gameSettingsViewModel.updateGameSettings(
            GameSettings(
                lastSettings.isSecondPlayerAi,
                gameMode.toString()
            )
        )
        presentChosenGame(gameMode)
    }

    //TODO Refactor
    private fun presentChosenGame(gameMode: GameMode) {
        loadAnimation.interpolator = OvershootInterpolator()

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

        val duration = 150L
        when(gameMode) {
            GameMode.TIC_TAC_TOE -> {
                game_choser_game_one_check.alpha = 1f
                game_choser_game_one_check.startAnimation(loadAnimation)
                game_choser_game_two_check.alpha = 0f
            }
            GameMode.FOUR_IN_A_ROW -> {
                game_choser_game_one_check.alpha = 0f
                game_choser_game_two_check.alpha = 1f
                game_choser_game_two_check.startAnimation(loadAnimation)
            }
        }
    }


    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_game_choser, this)
    }
}