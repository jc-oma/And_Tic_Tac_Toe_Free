package com.jcDevelopment.tictactoeadfree.module.companyLogo

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jcDevelopment.tictactoeadfree.R

class CompanyLogoFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            CompanyLogoFragment()
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

    override fun onResume() {
        super.onResume()

        startHomeFragmentAfterLoading()
    }

    private fun startHomeFragmentAfterLoading() {
        Handler().postDelayed({
            listener?.onLogoFragmentLoaded()
        }, 25)
    }

    interface Listener {
        fun onLogoFragmentLoaded()
    }
}