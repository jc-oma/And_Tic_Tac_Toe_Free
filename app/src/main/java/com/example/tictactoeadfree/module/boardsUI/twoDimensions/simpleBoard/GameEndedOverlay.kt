package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.overlay_two_dimension_simple_overlay.view.*

class GameEndedOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.overlay_two_dimension_simple_overlay, this)
    }

    fun onGameWon(){
        animateHeadline()
        ended_game_headline.text = context.getString(R.string.won)
    }

    fun onGameDraw(){
        animateHeadline()
        ended_game_headline.text = context.getString(R.string.draw)
    }

    private fun animateHeadline() {
        ended_game_headline.animate().duration = 600
        ended_game_headline.animate().rotationXBy(360f)
        ended_game_headline.animate().start()
    }
}