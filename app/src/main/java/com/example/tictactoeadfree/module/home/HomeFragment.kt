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
import com.example.tictactoeadfree.module.viewmodels.GameSettingsViewModel
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

        initiateClickListener()

        startIntroAnimation()
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
                home_headline.animate().alpha(1f).setDuration(alphaOffsetAppearance).withEndAction {
                    home_one_player_button.animate().alpha(1f).setDuration(alphaOffsetAppearance).withEndAction {
                        home_two_player_button.animate().alpha(1f).setDuration(alphaOffsetAppearance).start()
                    }.start()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        home_spooky_house_imageview.startAnimation(introAnimationBackGround)
    }

    private fun initiateClickListener() {
        home_one_player_button.setOnClickListener {
            listener?.onHomeFragmentButtonClick()
            settingViewModel.createGameSettings(GameSettings(true))
        }

        home_two_player_button.setOnClickListener {
            listener?.onHomeFragmentButtonClick()
            settingViewModel.createGameSettings(GameSettings(false))
        }
    }

    interface Listener {
        fun onHomeFragmentButtonClick()
    }
}