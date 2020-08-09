package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine
import kotlinx.android.synthetic.main.fragment_two_dimensions_simple_game.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TwoDimensionsSimpleGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TwoDimensionsSimpleGameFragment : Fragment(), TicTacToeEngine.GameListener {
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TwoDimensionsSimpleGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String = "", param2: String = "") =
            TwoDimensionsSimpleGameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onInitializeBoard()
        intializeBoardListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_two_dimensions_simple_game, container, false)
    }

    @DrawableRes
    private val oImgPlayerStone = R.drawable.blender_o_play_stone
    private val xImgPlayerStone = R.drawable.blender_x_play_stone

    private val toe: TicTacToeEngine by lazy {
        TicTacToeEngine(
            listener = this,
            context = requireContext()
        )
    }

    private lateinit var playGroundPositions: Pair<Float, Float>

    private val playGroundViewGrid: List<ImageView> by lazy {
        listOf(
            three_one,
            three_two,
            three_three,
            two_one,
            two_two,
            two_three,
            one_one,
            one_two,
            one_three
        )
    }

    //Todo make dynamic
    private val grid = 3

    private val placeHolderDrawable by lazy { requireContext().getDrawable(R.drawable.ic_spooky_kurbis) }

    //TODO move to lifecycle when converted into Fragment
    fun onCreateBoardAnimations() {

    }

    private fun getCurrentPlayerPlayStone(): Drawable? {
        return if (toe.getCurrentPlayer() == 1) requireContext().getDrawable(xImgPlayerStone) else requireContext().getDrawable(
            oImgPlayerStone
        )
    }

    private fun intializeBoardListener() {
        game_end_overlay.setOnClickListener {
            game_end_overlay.isVisible = false
        }

        prepareAnimationOnCreate()

        for ((index, cellView) in playGroundViewGrid.withIndex()) {
            startWhobbleAnimation(cellView)
            cellView.setImageDrawable(placeHolderDrawable)
            cellView.setOnClickListener {
                toe.playerTurn(index % grid, index / grid)
                cellView.setImageDrawable(getCurrentPlayerPlayStone())
                cellView.clearAnimation()
                cellView.setOnClickListener {}
            }
        }

        restart_game.whobbleAnimation(false)

        restart_game.setOnTouchListener { view, motionEvent ->
            restart_game.changeStyleOnTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                view.performClick()
                intializeBoardListener()
                toe.initializeBoard()
                for (cellView in playGroundViewGrid) {
                    cellView.setImageDrawable(placeHolderDrawable)
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun prepareAnimationOnCreate() {
        /*for (cell in playGroundViewGrid) {
            playGroundPositions = Pair(cell.x, cell.y)
            cell.animate().yBy(-2000f)
                .withEndAction{
                    cell.animate().x(playGroundPositions.first).y(playGroundPositions.second).setDuration(300L).start()
                }
                .start()
        }
         */
    }

    private fun startWhobbleAnimation(view: View) {
        val loadAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.whobble_animation
        )
        val randomDuration = ((Math.random() + 2) * 100).toLong()
        loadAnimation.duration = randomDuration

        view.startAnimation(
            loadAnimation
        )
    }

    override fun onGameEnd(wonPlayer: Int) {
        if (wonPlayer != 0) {
            game_info.text = requireContext().getString(R.string.player_x_won, wonPlayer.toString())
            game_end_overlay.onGameWon(wonPlayer)
        } else {
            game_info.text = requireContext().getString(R.string.draw)
            game_end_overlay.onGameDraw()
        }
        for (cellView in playGroundViewGrid) {
            cellView.clearAnimation()
        }
        game_end_overlay.isVisible = true
        game_end_overlay.onClosed()
        restart_game.whobbleAnimation(true)
        deleteBoardListener()
    }

    private fun deleteBoardListener() {
        playGroundViewGrid.forEach { id ->
            id.setOnClickListener {
                game_info.text = requireContext().getString(R.string.game_has_ended_hint)
                restart_game.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.shake_animation
                    )
                )
            }
        }
    }

    override fun onSwitchPlayer(playerNumber: Int) {
        game_info.text = playerNumber.toString()
    }

    override fun onInitializeBoard() {
        game_info.text = requireContext().getString(R.string.get_it_started)
    }
}