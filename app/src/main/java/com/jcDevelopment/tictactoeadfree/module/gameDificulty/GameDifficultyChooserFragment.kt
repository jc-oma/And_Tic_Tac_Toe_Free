package com.jcDevelopment.tictactoeadfree.module.gameDificulty

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameDifficulty
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.home.HomeFragment
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

        initListener()
    }

    private fun initListener() {
        difficulty_fragment_easy_button.setOnClickListener {
            gameSettingsViewModel.updateGameSettings(getGameSettings(GameDifficulty.EASY))
            listener?.onAiDifficultyChosen()
        }

        difficulty_fragment_medium_button.setOnClickListener {
            gameSettingsViewModel.updateGameSettings(getGameSettings(GameDifficulty.MEDIUM))
            listener?.onAiDifficultyChosen()
        }

        difficulty_fragment_hard_button.setOnClickListener {
            gameSettingsViewModel.updateGameSettings(getGameSettings(GameDifficulty.HARD))
            listener?.onAiDifficultyChosen()
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