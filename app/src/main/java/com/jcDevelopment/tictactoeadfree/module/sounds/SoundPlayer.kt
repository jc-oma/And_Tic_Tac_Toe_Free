package com.jcDevelopment.tictactoeadfree.module.sounds

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import com.jcDevelopment.tictactoeadfree.R

class SoundPlayer(val context: Context?) {
    companion object {
        private var soundPool: SoundPool? = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
    }

    init {
        soundPool!!.setOnLoadCompleteListener { soundPool, sampleId, _ ->
            soundPool.play(sampleId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playSound(id: Int) {
        val soundId: Int? = soundPool!!.load(context, id, 1)
    }
}