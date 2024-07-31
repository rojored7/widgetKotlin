package com.example.countwidget

import android.app.Application
import com.instana.android.Instana
import com.instana.android.core.InstanaConfig

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = InstanaConfig(
            reportingUrl = "https://your-instana-reporting-url",
            key = "your-instana-api-key"
        )
        Instana.setup(this, config)
    }
}