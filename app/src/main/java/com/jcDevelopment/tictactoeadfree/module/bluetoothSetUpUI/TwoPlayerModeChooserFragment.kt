package com.jcDevelopment.tictactoeadfree.module.bluetoothSetUpUI

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseFragment
import kotlinx.android.synthetic.main.fragment_two_player_mode_choser.*

class TwoPlayerModeChooserFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() = TwoPlayerModeChooserFragment()
    }

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
        return inflater.inflate(R.layout.fragment_two_player_mode_choser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        two_player_game_mode_bluetooth.isVisible = listener?.onCheckIfBluetoothAvailable() ?: false
    }

    private fun initListener() {
        two_player_game_mode_bluetooth.setOnClickListener { onBluetoothChosen() }
        two_player_game_mode_bluetooth_back_button.setOnClickListener { onBluetoothBack() }
        two_player_game_mode_hotseat.setOnClickListener { listener?.onTwoPlayerModeChooserFragmentHotseatClick() }

        two_player_game_mode_bluetooth_host.setOnClickListener { listener?.onBluetoothCreateHostButtonClicked() }
        two_player_game_mode_bluetooth_client.setOnClickListener { listener?.onBluetoothConnectToGameButtonClicked() }
    }

    private fun onBluetoothBack() {
        two_player_game_mode_second_step.isVisible = false
        val inAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.textswitcher_in
        )
        val outAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.textswitcher_out
        )
        two_player_game_mode_second_step.startAnimation(outAnimation)
        two_player_game_mode_first_step.startAnimation(inAnimation)
    }

    private fun onBluetoothChosen() {
        two_player_game_mode_second_step.isVisible = true
        val inAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.textswitcher_in
        )
        val outAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.textswitcher_out
        )
        two_player_game_mode_second_step.startAnimation(inAnimation)
        two_player_game_mode_first_step.startAnimation(outAnimation)
    }

    interface Listener {
        fun onTwoPlayerModeChooserFragmentHotseatClick()
        fun onCheckIfBluetoothAvailable():Boolean
        fun onBluetoothCreateHostButtonClicked()
        fun onBluetoothConnectToGameButtonClicked()
    }
}