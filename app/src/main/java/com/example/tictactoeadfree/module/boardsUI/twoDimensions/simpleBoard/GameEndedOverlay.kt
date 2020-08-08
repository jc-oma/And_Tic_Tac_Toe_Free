package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.view_overlay_two_dimension_simple_overlay.view.*
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class GameEndedOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_overlay_two_dimension_simple_overlay, this)
    }

    fun onGameWon(wonPlayer: Int) {
        animateHeadline()
        ended_game_headline.text =
            context.getString(R.string.player_x_won_headline, wonPlayer.toString())
    }

    fun onGameDraw() {
        animateHeadline()
        ended_game_headline.text = context.getString(R.string.draw)
    }

    fun onClosed() {
        ended_game_konfetti_view.stopGracefully()
    }

    private fun animateHeadline() {
        ended_game_headline
            .animate()
            .setDuration(600)
            .rotationXBy(360f)
            .withEndAction{setupKonfettiView()}
            .start()
    }

    private fun setupKonfettiView() {
        ended_game_konfetti_view.build()
                //TODO theme colors
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 10f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.DrawableShape(context.getDrawable(R.drawable.ic_spooky_bat)!!))
            .addSizes(Size(80))
            .setPosition(
                ended_game_headline.x + ended_game_headline.width,
                ended_game_headline.y
            )
            .burst(60)
    }
}