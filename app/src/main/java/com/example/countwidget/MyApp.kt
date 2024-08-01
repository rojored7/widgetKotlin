package com.example.countwidget

import android.app.Application
import android.util.Log
import com.instana.android.Instana
import com.instana.android.core.InstanaConfig

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = InstanaConfig(reportingURL = "https://eum-red-saas.instana.io/mobile",
            key = "41HBybUnT_C4WE9cCOnKGA"
        )
        Instana.setup(this, config)

        // Verificar la conectividad con Instana
        verifyInstanaConnectivity()
    }
    fun verifyInstanaConnectivity() {
        val trackingData = mapOf(
            "event" to "TestEvent",
            "description" to "Testing Instana integration"
        )
        Instana.meta.putAll(trackingData)

        // Log para verificar que se envió el evento
        Log.d("MyApp", "Instana connectivity test event sent: $trackingData")
    }
}