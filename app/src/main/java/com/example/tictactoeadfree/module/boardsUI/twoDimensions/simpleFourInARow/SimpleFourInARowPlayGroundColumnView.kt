package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R

class SimpleFourInARowPlayGroundColumnView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_four_in_a_row_column, this)
    }
}