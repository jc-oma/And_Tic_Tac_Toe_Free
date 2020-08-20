package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.view_board_two_dimensions_simple.view.*
import kotlinx.android.synthetic.main.view_four_in_a_row_column.view.*

class SimpleFourInARowPlayGroundColumnView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        initView(context)
    }

    private val playGroundViewColumnList: List<ImageView> by lazy {
        listOf(
            four_in_a_row_column_first,
            four_in_a_row_column_second,
            four_in_a_row_column_third,
            four_in_a_row_column_fourth,
            four_in_a_row_column_fifth,
            four_in_a_row_column_sixth
        )
    }

    private val playGroundViewColumnPositionList: MutableList<Pair<Float, Float>> = mutableListOf()

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_four_in_a_row_column, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        initListener()
        getColumnCoordinates()
        initAnmation()
    }

    private fun getColumnCoordinates() {
        for ((index, view) in playGroundViewColumnList.withIndex()) {
            view.viewTreeObserver.addOnGlobalLayoutListener {
                playGroundViewColumnPositionList.add(index, Pair(view.x, view.y))
            }
        }
    }

    private fun initAnmation() {
        val whobbleAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.whobble_animation_medium
        )
        four_in_a_row_column_image_button.startAnimation(whobbleAnimation)
    }

    private fun initListener() {
        four_in_a_row_column_image_button.setOnClickListener {
            val x = playGroundViewColumnPositionList[5].first
            val y = playGroundViewColumnPositionList[5].second
            four_in_a_row_column_playstone_1.animate().x(x).y(y).start()
        }
    }
}