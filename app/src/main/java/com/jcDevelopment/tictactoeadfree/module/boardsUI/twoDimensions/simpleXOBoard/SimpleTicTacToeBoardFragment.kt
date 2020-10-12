package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseFragment
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_two_dimensions_simple_game.*
import org.koin.android.ext.android.inject


class SimpleTicTacToeBoardFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            SimpleTicTacToeBoardFragment()
    }

    private var showInterstitialAdDisposable: Disposable? = null
    private var isGameEndedDisposable: Disposable? = null

    private val gameStatisticsViewModel by inject<GameStatisticsViewModel>()

    private var listener: Listener? = null

    private var isGameEnded = false

    override fun onDestroy() {
        super.onDestroy()
        val playedGames = gameStatisticsViewModel.getGameStatisticsList().size
        if (isGameEnded && playedGames % 3 == 0)  {
            listener?.onTicTacToeInterstitialAd()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_two_dimensions_simple_game, container, false)
    }

    override fun onResume() {
        super.onResume()
        play_board?.backPressEvent?.subscribe {
            if (it) {
                this.activity?.onBackPressed()
            }
        }

        showInterstitialAdDisposable = play_board?.showInterstitialAd?.doOnNext {
            if (it) {
                listener?.onTicTacToeInterstitialAd()
            }
        }?.subscribe()

        isGameEndedDisposable = play_board?.isGameEnded?.doOnNext {
            isGameEnded = it
        }?.subscribe()
    }

    override fun onPause() {
        super.onPause()

        showInterstitialAdDisposable?.dispose()
        isGameEndedDisposable?.dispose()
    }

    interface Listener {
        fun onTicTacToeInterstitialAd()
    }
}