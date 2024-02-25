package com.example.kotlinjetpack

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class App : Application() {
    companion object {
        var application: Application? = null
    }

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        application = this
    }

    fun getAppContext() = application?.applicationContext
}