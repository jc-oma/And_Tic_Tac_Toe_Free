package com.example.tictactoeadfree.module.gameButtons

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.view_button_edged.view.*

class EdgedGameButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_button_edged, this)
    }

    fun whobbleAnimation(isWhobble: Boolean) {
        if (isWhobble) {
            edge_button_root.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.whobble_animation_little
                )
            )
        } else {
            edge_button_root.clearAnimation()
        }
    }

    fun changeStyleOnTouchEvent(motionEvent: MotionEvent) {
        val clickOutDuration = 150L
        val clickInDuration = 30L
        val scaleTo = 0.9f
        val scaleFrom = 1f

        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            edged_button_image.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.blender_buttonedge2_touched
                )
            )
            edge_button_root.animate().scaleX(scaleTo).setInterpolator(BounceInterpolator())
                .setDuration(
                    clickInDuration
                ).start()
            edge_button_root.animate().scaleY(scaleTo).setInterpolator(BounceInterpolator())
                .setDuration(
                    clickInDuration
                ).start()
        } else if (motionEvent.action == MotionEvent.ACTION_UP) {
            edged_button_image.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.blender_buttonedge2
                )
            )
            edge_button_root.animate().scaleX(scaleFrom).setInterpolator(BounceInterpolator())
                .setDuration(
                    clickOutDuration
                ).start()
            edge_button_root.animate().scaleY(scaleFrom).setInterpolator(BounceInterpolator())
                .setDuration(
                    clickOutDuration
                ).start()
        }
    }
}