package com.quizroom

import android.app.Activity
import android.app.Application
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import com.quizroom.utils.Utils.Companion.isAudioEnabled

class QuizApp: Application(), Application.ActivityLifecycleCallbacks {

    companion object{
        var musicPlayer: MediaPlayer? = null
    }
    private var activityReferences = 0
    private var isActivityChangingConfigurations = false
    private lateinit var filter: IntentFilter

    override fun onCreate() {
        super.onCreate()
        musicPlayer = MediaPlayer.create(this, R.raw.game_play_music)
        musicPlayer!!.isLooping = true
        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        if (++activityReferences == 1) {
            // App enters foreground
            if (!musicPlayer!!.isPlaying && isAudioEnabled && activity.localClassName!="MainActivity"){
                musicPlayer!!.start()
            }
        }
    }

    override fun onActivityPaused(p0: Activity) {
        if (--activityReferences == 0) {
            // App enters background
            if (musicPlayer!!.isPlaying){
                musicPlayer!!.pause()
            }
        }
    }

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}
}