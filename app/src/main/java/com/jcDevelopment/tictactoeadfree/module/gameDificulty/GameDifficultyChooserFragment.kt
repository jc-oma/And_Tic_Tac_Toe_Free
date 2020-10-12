package com.jcDevelopment.tictactoeadfree.module.gameDificulty

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameDifficulty
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.sounds.SoundPlayer
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import kotlinx.android.synthetic.main.fragment_game_difficulty_choser.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class GameDifficultyChooserFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            GameDifficultyChooserFragment().apply {}
    }

    private val gameSettingsViewModel: GameSettingsViewModel by viewModel()

    private var listener: Listener? = null

    private val checkIconsList by lazy {
        listOf(
            difficult_fragment_easy_frame_check,
            difficult_fragment_mid_frame_check,
            difficult_fragment_hard_frame_check
        )
    }

    private val soundPlayer by lazy { SoundPlayer.getInstance(context!!) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString())
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_difficulty_choser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkIfHardModeAvailable()

        //checkIfHorizontalViewIsScrollable()

        initListener()
    }

    /*private fun checkIfHorizontalViewIsScrollable() {
        var scrollViewWidth: Int?
        var childViewWidth: Int?
        difficult_fragment_horizontalScrollView?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                scrollViewWidth = difficult_fragment_horizontalScrollView?.width
                childViewWidth = difficult_fragment_horizontalScrollView_child_view?.width

                scrollViewWidth?.let { sv -> childViewWidth?.let { child ->
                    if (sv < child) {
                        Toast.makeText(context, "scrollable", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "not scrollable", Toast.LENGTH_LONG).show()
                    }
                    difficult_fragment_horizontalScrollView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                } }
            }
        })
    }*/

    override fun onResume() {
        super.onResume()

        showLastChosenDiff()
    }

    private fun showLastChosenDiff() {
        val lastDiffSetting = GameDifficulty.valueOf(
            gameSettingsViewModel.getGameSettings()
                .last().difficulty
        )

        when (lastDiffSetting) {
            GameDifficulty.EASY -> setCheckIconOnPositionVisible(0)
            GameDifficulty.MEDIUM -> setCheckIconOnPositionVisible(1)
            GameDifficulty.HARD -> {
                setCheckIconOnPositionVisible(2)
                difficult_fragment_horizontalScrollView.scrollTo(
                    difficult_fragment_horizontalScrollView.right, 0
                )
            }
        }
    }

    private fun setCheckIconOnPositionVisible(i: Int) {
        for ((index, icon) in checkIconsList.withIndex()) {
            if (index == i) {
                icon.visibility = View.VISIBLE
            } else {
                icon.visibility = View.GONE
            }
        }
    }

    private fun checkIfHardModeAvailable() {
        if (gameSettingsViewModel.getGameSettings()
                .last().gameMode == GameMode.FOUR_IN_A_ROW.toString()
        ) {
            difficult_fragment_hard_frame_constraint.visibility = View.GONE
            difficult_fragment_mid_to_hard_arrows.visibility = View.GONE
        } else {
            difficult_fragment_hard_frame_constraint.visibility = View.VISIBLE
            difficult_fragment_mid_to_hard_arrows.visibility = View.VISIBLE
        }
    }

    private fun initListener() {
        difficulty_fragment_start_button.setOnClickListener {
            listener?.onAiDifficultyChosen()
        }

        difficult_fragment_easy_frame_constraint.setOnClickListener {
            gameSettingsViewModel.updateGameSettings(getGameSettings(GameDifficulty.EASY))
            setCheckIconOnPositionVisible(0)

            difficult_fragment_horizontalScrollView.scrollTo(
                difficult_fragment_horizontalScrollView.left, 0
            )

            soundPlayer.playLoadedSound(SoundPlayer.SoundList.CLICK_FEED_BACK)
        }

        difficult_fragment_mid_frame_constraint.setOnClickListener {
            gameSettingsViewModel.updateGameSettings(getGameSettings(GameDifficulty.MEDIUM))
            setCheckIconOnPositionVisible(1)

            soundPlayer.playLoadedSound(SoundPlayer.SoundList.CLICK_FEED_BACK)
        }

        difficult_fragment_hard_frame_constraint.setOnClickListener {
            gameSettingsViewModel.updateGameSettings(getGameSettings(GameDifficulty.HARD))
            setCheckIconOnPositionVisible(2)

            difficult_fragment_horizontalScrollView.scrollTo(
                difficult_fragment_horizontalScrollView.right, 0
            )

            soundPlayer.playLoadedSound(SoundPlayer.SoundList.CLICK_FEED_BACK)
        }
    }

    private fun getGameSettings(difficulty: GameDifficulty): GameSettings {
        val gameSettings = gameSettingsViewModel.getGameSettings().first()
        return GameSettings(
            isSecondPlayerAi = gameSettings.isSecondPlayerAi,
            gameMode = gameSettings.gameMode,
            difficulty = difficulty.toString()
        )
    }

    interface Listener {
        fun onAiDifficultyChosen()
    }
}