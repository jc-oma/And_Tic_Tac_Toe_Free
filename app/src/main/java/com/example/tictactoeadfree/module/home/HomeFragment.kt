package com.example.tictactoeadfree.module.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.baseClasses.BaseFragment
import com.example.tictactoeadfree.module.data.gameSettings.GameSettings
import com.example.tictactoeadfree.module.data.gameSettings.gameSettingsModule
import com.example.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.google.android.gms.ads.AdRequest
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

        initToolbar()

        initiateClickListener()

        startIntroAnimation()
    }

    private fun initAds() {
        val adRequest = AdRequest.Builder().build()
        home_fragment_ad_view.loadAd(adRequest)
    }

    private fun initToolbar() {
        home_toolbar.inflateMenu(R.menu.menu_fragment_home)
        home_toolbar.title = context?.getText(R.string.app_name)
        home_toolbar.setTitleTextAppearance(context, R.style.berkshireTextStyle)
    }

    private fun startIntroAnimation() {
        val startOffsetBegin: Long = 1000
        val introAnimationBackGround = AnimationUtils.loadAnimation(
            context,
            R.anim.intro_animation_background
        )

        introAnimationBackGround.fillAfter = true
        introAnimationBackGround.startOffset = startOffsetBegin
        introAnimationBackGround.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                val alphaOffsetAppearance = 400L
                home_spooky_witch_imageview.animate().alpha(1f)
                    .setDuration(alphaOffsetAppearance).start()
                home_one_player_button.animate().alpha(1f).setDuration(alphaOffsetAppearance)
                    .withEndAction {
                        home_spooky_ghost_imageview.animate().alpha(1f)
                            .setDuration(alphaOffsetAppearance).start()
                        home_spooky_ghost_imageview_2.animate().alpha(1f)
                            .setDuration(alphaOffsetAppearance).start()
                        home_two_player_button.animate().alpha(1f)
                            .setDuration(alphaOffsetAppearance).withEndAction {
                                home_game_choser.animate().alpha(1f)
                                    .setDuration(alphaOffsetAppearance).start()
                            }
                            .start()
                    }.start()
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        home_spooky_house_imageview.startAnimation(introAnimationBackGround)
    }

    private fun initiateClickListener() {
        home_one_player_button.setOnClickListener {
            val lastGameSettings = if (settingViewModel.getGameSettings().isEmpty()) {
                GameSettings()
            } else {
                settingViewModel.getGameSettings().last()
            }
            listener?.onHomeFragmentButtonClick()
            settingViewModel.createGameSettings(GameSettings(true, lastGameSettings.gameMode))
        }

        home_two_player_button.setOnClickListener {
            val lastGameSettings = settingViewModel.getGameSettings().last()
            listener?.onHomeFragmentButtonClick()
            settingViewModel.createGameSettings(GameSettings(false, lastGameSettings.gameMode))
        }
    }

    interface Listener {
        fun onHomeFragmentButtonClick()
    }
}