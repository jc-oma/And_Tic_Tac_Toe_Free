package com.jcDevelopment.tictactoeadfree.module.music

import android.content.Context
import android.media.MediaPlayer
import com.jcDevelopment.tictactoeadfree.R

class MusicPlayer (val context: Context) {
    companion object{
        val playlist by lazy {
            listOf(
                R.raw.mp3_10__red_pumpkin,
                R.raw.mp3_1__musicbox,
                R.raw.mp3_2__early_halloween,
                R.raw.mp3_3__corspe_ballet,
                R.raw.mp3_4__candy_factory,
                R.raw.mp3_5__haunted_candle,
                R.raw.mp3_6__spooky_wind,
                R.raw.mp3_7__sneaky,
                R.raw.mp3_8__phantom_palace,
                R.raw.mp3_9__inside_a_ghost
            )
        }
    }

    private var mediaPlayer :MediaPlayer = MediaPlayer()

    private val randomSong = (Math.random() * playlist.size - 1).toInt()

    fun initMediaPlayer(index: Int = randomSong) {
        var localIndex = index
        mediaPlayer = MediaPlayer.create(context, playlist[localIndex])

        mediaPlayer.setOnCompletionListener {
            if (localIndex < playlist.size - 1) {
                localIndex++
            } else {
                localIndex = 0
            }

            initMediaPlayer(localIndex)
            resumeMusic()
        }
    }

    fun pauseMusic() {
        mediaPlayer.stop()
    }

    fun resumeMusic() {
        mediaPlayer.start()
    }

    fun stopMusic() {
        mediaPlayer.release()
    }
}