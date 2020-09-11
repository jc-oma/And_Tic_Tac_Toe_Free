package com.jcDevelopment.tictactoeadfree.module.bluetoothSetUpUI

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.jakewharton.rxbinding4.view.clicks
import com.jcDevelopment.tictactoeadfree.R
import kotlinx.android.synthetic.main.view_waiting_for_player_as_host.view.*

class WaitingForPlayerAsHostView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_waiting_for_player_as_host, this)
    }

    val closeObservable = waiting_for_player_back_button.clicks()

    override fun onFinishInflate() {
        super.onFinishInflate()
        val infiniteRotationAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.infinite_rotation
        )
        waiting_for_player_loading_spinner.startAnimation(infiniteRotationAnimation)
    }
}