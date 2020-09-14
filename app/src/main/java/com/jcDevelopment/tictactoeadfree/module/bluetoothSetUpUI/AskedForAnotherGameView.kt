package com.jcDevelopment.tictactoeadfree.module.bluetoothSetUpUI

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.jakewharton.rxbinding4.view.clicks
import com.jcDevelopment.tictactoeadfree.R
import kotlinx.android.synthetic.main.view_asked_for_another_game.view.*

class AskedForAnotherGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_asked_for_another_game, this)
    }

    val ackAnotherGameObservable by lazy { asked_for_another_ack_button?.clicks() }
    val declineAnotherGameObservable by lazy { asked_for_another_decline_button?.clicks() }

    fun setHeadline(s:String?){
        s?.let { opponent_left_info_headline.text = context.getString(R.string.ask_another_game_headline, it)}
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }
}