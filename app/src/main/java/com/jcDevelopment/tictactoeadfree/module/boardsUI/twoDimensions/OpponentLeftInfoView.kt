package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.gameEngine.tictactoe.TicTacToeEngine
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_opponent_left_info.view.*
import org.koin.core.KoinComponent

class OpponentLeftInfoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        initView(context)
    }

    val backPressEvent: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_opponent_left_info, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        opponent_left_info_ack_button.setOnClickListener {
            backPressEvent.onNext(true)
        }
    }
}