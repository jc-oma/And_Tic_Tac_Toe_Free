package com.jcDevelopment.tictactoeadfree.module.bluetooth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseFragment
import com.jcDevelopment.tictactoeadfree.module.home.HomeFragment
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
        initListener()
    }

    private fun initListener() {
        two_player_game_mode_bluetooth.setOnClickListener { listener?.onTwoPlayerModeChooserFragmentBluetoothClick() }
        two_player_game_mode_hotseat.setOnClickListener { listener?.onTwoPlayerModeChooserFragmentBluetoothClick() }
    }

    interface Listener {
        fun onTwoPlayerModeChooserFragmentBluetoothClick()
        fun onTwoPlayerModeChooserFragmentHotseatClick()
    }
}