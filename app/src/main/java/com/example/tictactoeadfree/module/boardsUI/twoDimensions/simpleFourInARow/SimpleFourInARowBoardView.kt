package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_board_four_in_a_row_simple.view.*

class SimpleFourInARowBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), TicTacToeEngine.GameListener {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_board_four_in_a_row_simple, this)
    }

    private val toe: TicTacToeEngine =
        TicTacToeEngine(listener = this)

    private val playGroundViewGrid: List<SimpleFourInARowPlayGroundColumnView> by lazy {
        listOf(
            bord_row_1,
            bord_row_2,
            bord_row_3,
            bord_row_4,
            bord_row_5,
            bord_row_6,
            bord_row_7
        )
    }

    private val gridx = 7
    private val gridy = 6

    override fun onFinishInflate() {
        super.onFinishInflate()
        initListener()
    }

    private fun initListener() {
        initRowClickListener()
    }

    private fun initRowClickListener() {
        for ((index, view) in playGroundViewGrid.withIndex()){
            view.click.observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.animatePlayStoneDrop(3)
                }
        }
    }

    override fun onGameEnd(wonPlayer: Int, wonPosition: MutableList<Triple<Int, Int, Int>>?) {
        TODO("Not yet implemented")
    }

    override fun onSwitchPlayer(playerNumber: Int) {
    }

    override fun onInitializeBoard() {
    }

    override fun onAiIsTurning() {
    }

    override fun onPlayerTurned(
        positionX: Int,
        positionY: Int,
        positionZ: Int,
        currentPlayer: Int
    ) {

    }

}