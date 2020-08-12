package com.example.tictactoeadfree.module.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }

    private var listener: Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() )
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

        home_one_player_button.setOnClickListener{
            listener?.onHomeFragmentButtonClick()
        }

        home_two_player_button.setOnClickListener{
            listener?.onHomeFragmentButtonClick()
        }
    }

    interface Listener {
        fun onHomeFragmentButtonClick()
    }
}