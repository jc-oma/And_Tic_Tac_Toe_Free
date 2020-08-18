package com.example.tictactoeadfree.module.logo

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.home.HomeFragment
import kotlinx.android.synthetic.main.fragment_logo.*

class LogoFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            LogoFragment()
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
        return inflater.inflate(R.layout.fragment_logo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startHomeFragmentAfterLoading()
    }

    private fun startHomeFragmentAfterLoading() {
        Handler().postDelayed({
            listener?.onLogoFragmentLoaded()
        }, 2500)
    }

    interface Listener {
        fun onLogoFragmentLoaded()
    }
}