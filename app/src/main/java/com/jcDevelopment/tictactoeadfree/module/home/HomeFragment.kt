package com.jcDevelopment.tictactoeadfree.module.home

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseFragment
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.google.android.gms.ads.AdRequest
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerMode
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettings
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject


class HomeFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }

    private var listener: Listener? = null

    private val settingViewModel by inject<GameSettingsViewModel>()
    private val multiplayerSettingsViewModel by inject<MultiplayerSettingsViewModel>()

    private var switchViewCount = 0
    private var isSecondPlayerAi = true

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAds()

        initiateClickListener()

        startIntroAnimation()
    }

    override fun onResume() {
        super.onResume()

        initSettings()
        initSettingsPresentation()
    }

    private fun initSettings() {
        multiplayerSettingsViewModel.updateMultiplayersettings(MultiplayerSettings(multiplayerMode = MultiplayerMode.NONE.toString()))
    }

    private fun initSettingsPresentation() {
        val gameSettings = settingViewModel.getGameSettings()
        if (gameSettings.isNotEmpty()) {
            val lastSettings = gameSettings.last()
            isSecondPlayerAi = lastSettings.isSecondPlayerAi
            if (isSecondPlayerAi) {
                switchViewCount = 2
                home_player_toggle_text_switcher.setText(getString(R.string.one_player))
            } else {
                switchViewCount = 1
                home_player_toggle_text_switcher.setText(getString(R.string.two_player))
            }
        }
    }

    private fun initAds() {
        val adRequest = AdRequest.Builder().build()
        home_fragment_ad_view.loadAd(adRequest)
    }

    private fun startIntroAnimation() {
        val toBeAnimatedViews = listOf(
            home_spooky_background_anim_imageview,
            home_spooky_house_imageview,
            home_spooky_ghost_imageview_2,
            home_lightning_sequence,
            home_player_toggle_text_switcher,
            home_game_choser,
            home_start_game_button
        )

        toBeAnimatedViews.forEach {
            it.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }

        val animation = home_lightning_sequence.background as AnimationDrawable
        animation.start()

        val startOffsetBegin: Long = 1000
        val introAnimationBackGround = AnimationUtils.loadAnimation(
            context,
            R.anim.intro_animation_background
        )

        introAnimationBackGround.fillAfter = isSecondPlayerAi
        introAnimationBackGround.startOffset = startOffsetBegin
        introAnimationBackGround.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val alphaOffsetAppearance = 400L
                home_spooky_ghost_imageview.animate().alpha(1f)
                    .setDuration(alphaOffsetAppearance).start()
                if (settingViewModel.getGameSettings()
                        .isNotEmpty() && !settingViewModel.getGameSettings().last().isSecondPlayerAi
                ) {
                    home_spooky_ghost_imageview_2.animate().alpha(1f)
                        .setDuration(alphaOffsetAppearance).start()
                }
                home_player_toggle_text_switcher.animate().alpha(1f)
                    .setDuration(alphaOffsetAppearance).withEndAction {
                        home_game_choser.animate().alpha(1f)
                            .setDuration(alphaOffsetAppearance).withEndAction {
                                home_start_game_button?.animate()?.alpha(1f)
                                    ?.setDuration(alphaOffsetAppearance)?.start()
                            }.start()
                    }.start()
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        home_spooky_house_imageview.startAnimation(introAnimationBackGround)
    }

    private fun initiateClickListener() {
        Handler().postDelayed({
            home_spooky_house_imageview?.performClick()
        }, 6000)

        home_player_toggle_text_switcher.setOnClickListener {
            switchViewCount++
            if (switchViewCount % 2 == 0) {
                isSecondPlayerAi = true
                home_spooky_ghost_imageview_2.alpha = 0f
                home_player_toggle_text_switcher.setText(getString(R.string.one_player))
                settingViewModel.updateGameSettings(
                    GameSettings(
                        isSecondPlayerAi,
                        getLastGameSettings().gameMode
                    )
                )
            } else {
                isSecondPlayerAi = false
                home_spooky_ghost_imageview_2.alpha = 1f
                home_player_toggle_text_switcher.setText(getString(R.string.two_player))
                settingViewModel.updateGameSettings(
                    GameSettings(
                        isSecondPlayerAi,
                        getLastGameSettings().gameMode
                    )
                )
            }
        }

        home_spooky_ghost_imageview_click_holder.setOnClickListener {
            home_player_toggle_text_switcher.performClick()
        }

        home_start_game_button.setOnClickListener {
            val lastGameSettings = getLastGameSettings()
            settingViewModel.updateGameSettings(
                GameSettings(
                    isSecondPlayerAi = isSecondPlayerAi,
                    gameMode = lastGameSettings.gameMode,
                    difficulty = lastGameSettings.difficulty
                )
            )

            listener?.onHomeFragmentGameStartButtonClick()
        }
    }

    private fun getLastGameSettings(): GameSettings {
        val gameSettings = settingViewModel.getGameSettings()
        return if (gameSettings.isEmpty()) {
            GameSettings()
        } else {
            gameSettings.last()
        }
    }

    interface Listener {
        fun onHomeFragmentGameStartButtonClick()
    }
}