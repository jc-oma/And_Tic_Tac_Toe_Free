package com.jcDevelopment.tictactoeadfree.module.statistics

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode

class StatisticsUtils(val context: Context?) {

    fun getDrawablesPair(gameMode: GameMode): Pair<Drawable?, Drawable?>? {
        context?.let {
            if (gameMode == GameMode.FOUR_IN_A_ROW) {
                val first = ContextCompat.getDrawable(it, R.drawable.ic_spooky_kurbis_v3_3d)
                val second =
                    ContextCompat.getDrawable(it, R.drawable.ic_spooky_kurbis_v3_3d_tinted)
                return Pair(first, second)
            } else {
                val first = ContextCompat.getDrawable(it, R.drawable.blender_x_play_stone)
                val second =
                    ContextCompat.getDrawable(it, R.drawable.blender_o_play_stone)
                return Pair(first, second)
            }
        } ?: return null
    }
}