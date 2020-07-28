package com.kaito.afinal

import android.app.Application

class LolApp:Application() {
    override fun onCreate() {
        super.onCreate()
        DarkModeHelper.getInstance(this)
    }
}