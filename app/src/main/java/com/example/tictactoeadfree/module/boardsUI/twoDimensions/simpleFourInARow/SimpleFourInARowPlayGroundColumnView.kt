package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.logo.LogoFragment
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_board_two_dimensions_simple.view.*
import kotlinx.android.synthetic.main.view_four_in_a_row_column.view.*
import java.lang.reflect.Array.set

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

    val click: Observable<Unit> by lazy { four_in_a_row_column_image_button.clicks() }

    private val playGroundViewColumnPositionList: MutableList<Pair<Float, Float>> = mutableListOf()

    override fun onFinishInflate() {
        super.onFinishInflate()
        getColumnCoordinates()
        initAnmation()
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_four_in_a_row_column, this)
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

    fun animatePlayStoneDrop(toRow: Int) {
        val x = playGroundViewColumnPositionList[toRow].first
        val y = playGroundViewColumnPositionList[toRow].second
        four_in_a_row_column_playstone_1.animate().x(x).y(y).start()
    }
}