package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewManager
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.jcDevelopment.tictactoeadfree.R
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.view_four_in_a_row_column.view.*
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextBoolean


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

    private val createdPlaystones: MutableList<View> = mutableListOf()

    val click: Observable<Unit> by lazy { four_in_a_row_column_root.clicks() }

    private val playGroundViewColumnPositionList: MutableList<Pair<Float, Float>> = mutableListOf()

    fun animateStackElement(toAnimateElementIndex: Int) {
        val animation =
            playGroundViewColumnList[toAnimateElementIndex].background as AnimationDrawable
        animation.start()
    }

    fun restartBoard() {
        var offset = 100L
        for (stone in createdPlaystones) {
            val animation = stone.animate()
            animation.y(0f)
            animation.alpha(0.5f)
            animation.duration = 200
            animation.interpolator = AnticipateInterpolator()
            animation.withLayer()
            if (offset < 1000L) {
                offset += (offset * Math.random()).toLong()
            }
            animation.setStartDelay(offset)
            animation.withEndAction { (stone.parent as ViewManager).removeView(stone) }
        }

        for (stackElement in playGroundViewColumnList) {
            val stackAnimation = stackElement.background as AnimationDrawable
            stackAnimation.stop()
            stackAnimation.selectDrawable(0)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        getColumnCoordinates()
        initAnmation()
    }

    fun animatePlayStoneDrop(toRow: Int, currentPlayer: Int) {
        createdPlaystones.add(createNewPlayStoneView(currentPlayer))
        val x = playGroundViewColumnPositionList[toRow].first
        val y = playGroundViewColumnPositionList[toRow].second
        val animation = createdPlaystones.last().animate().withLayer()
        val randomRotationDegree = Math.random() * 360f
        val randomRotationdirection = if (nextBoolean()) 1 else -1
        animation.duration = 400L * (sqrt(toRow.toDouble()).toLong() + 1)
        animation.interpolator = BounceInterpolator()
        animation.rotationBy((randomRotationDegree * randomRotationdirection).toFloat())
        animation.x(x).y(y)
        animation.start()
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
        four_in_a_row_column_image_button.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val whobbleAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.whobble_animation_medium
        )
        four_in_a_row_column_image_button.startAnimation(whobbleAnimation)
    }

    private fun createNewPlayStoneView(currentPlayer: Int): ImageView {
        val widthAndHeightDP = 50f
        val marginBottomDP = 5f

        val imageView = ImageView(context)
        imageView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val scale =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                widthAndHeightDP,
                resources.displayMetrics
            )
                .toInt()
        val marginBottom =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                marginBottomDP,
                resources.displayMetrics
            )
                .toInt()

        val params = LayoutParams(
            scale,
            scale
        )

        params.setMargins(0, 0, 0, marginBottom)
        imageView.layoutParams = params

        imageView.id = View.generateViewId()
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setImageResource(getCurrentPlayerStone(currentPlayer))
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

    private fun getCurrentPlayerStone(currentPlayer: Int): Int {
        return if (currentPlayer == 1) {
            R.drawable.ic_spooky_kurbis_v3_3d
        } else R.drawable.ic_spooky_kurbis_v3_3d_tinted
    }
}