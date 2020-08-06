package com.example.tictactoeadfree.module.gameButtons

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
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

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun changeStyleOnTouchEvent(motionEvent: MotionEvent) {
        if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            edged_button_image.setImageDrawable(context.getDrawable(R.drawable.blender_buttonedge2_touched))
        } else if (motionEvent.action == MotionEvent.ACTION_UP) {
            edged_button_image.setImageDrawable(context.getDrawable(R.drawable.blender_buttonedge2))
        }
    }
}