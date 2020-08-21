package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.tictactoeadfree.R
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
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

    val click: Observable<Unit> by lazy { four_in_a_row_column_root.clicks() }

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
        val playStone = createNewPlayStoneView()
        val x = playGroundViewColumnPositionList[toRow].first
        val y = playGroundViewColumnPositionList[toRow].second
        playStone.animate().x(x).y(y).start()
    }

    private fun createNewPlayStoneView(): ImageView {
        val widthAndHeightDP = 50f
        val marginBottomDP = 5f

        val imageView = ImageView(context)
        val scale =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthAndHeightDP, resources.displayMetrics)
                .toInt()
        val marginBottom =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginBottomDP, resources.displayMetrics)
                .toInt()

        val params = LayoutParams(
            scale,
            scale
        )

        params.setMargins(0, 0, 0, marginBottom)
        imageView.layoutParams = params

        imageView.id = View.generateViewId()
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageResource(R.drawable.ic_spooky_kurbis_v3_3d_tinted)
        four_in_a_row_column_root.addView(imageView)

        val set = ConstraintSet()
        set.clone(four_in_a_row_column_root)

        set.connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        set.connect(
            imageView.id,
            ConstraintSet.BOTTOM,
            four_in_a_row_column_first.id,
            ConstraintSet.TOP
        )
        set.applyTo(four_in_a_row_column_root)
        return imageView
    }
}