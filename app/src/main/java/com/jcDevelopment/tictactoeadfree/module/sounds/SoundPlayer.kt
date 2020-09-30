package com.jcDevelopment.tictactoeadfree.module.sounds

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.jcDevelopment.tictactoeadfree.R

class SoundPlayer(val context: Context?) {
    companion object {
        @Volatile
        private var INSTANCE: SoundPlayer? = null

        fun getInstance(context: Context): SoundPlayer =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundPlayer(context).also { INSTANCE = it }
            }

        private var soundPool: SoundPool? = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    }

    private val fiarStoneDropSound by lazy {
        listOf(
            soundPool?.load(context, R.raw.mp3_fiar_stone_drop, 1),
            soundPool?.load(context, R.raw.mp3_click_feedback, 1),
            soundPool?.load(context, R.raw.mp3_frankenstein_thinking, 1),
            soundPool?.load(context, R.raw.mp3_witch_laughing, 1),
            soundPool?.load(context, R.raw.mp3_init_sound, 1)
        )
    }

    enum class SoundList(val value: Int) {
        FIAR_STONE_DROP(0),
        CLICK_FEED_BACK(1),
        FRANKENSTEIN_THINKING(2),
        WITCH_LAUGHING(3),
        INIT_SOUND(4)
    }

    fun playLoadedSound(id: SoundList, volume: Float = 1f) {
        fiarStoneDropSound[id.value]?.let {
            soundPool?.play(it, volume, volume, 0, 0, 1f)
        }
    }
}
