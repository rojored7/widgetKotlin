package com.example.countwidget

import android.app.Application
import android.util.Log
import com.instana.android.CustomEvent
import com.instana.android.Instana
import com.instana.android.core.InstanaConfig


class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = InstanaConfig(
            reportingURL = "https://eum-coral-saas.instana.io/mobile",
            key = "8sJJ9fdWRreiiF8Opy6MmA"
        )
        Instana.setup(this, config)

        // Verificar la conectividad con Instana al iniciar la aplicación
        verifyInstanaConnectivity()
    }

    companion object {
        fun verifyInstanaConnectivity() {
            val trackingData = mapOf(
                "event" to "TestEvent",
                "description" to "Testing Instana integration"
            )
            Instana.reportEvent(
                CustomEvent(eventName = "TestEvent"

                )
            )

            // Enviar confirmación de conexión exitosa
            val successData = mapOf(
                "event" to "ConnectionSuccess",
                "description" to "Instana connectivity test successful"
            )
            Instana.reportEvent(
                CustomEvent(eventName = "ConnectionSuccess"

                )
            )

            // Log para verificar que se envió el evento
            Log.d("MyApp", "Instana connectivity test event sent: $trackingData")
            Log.d("MyApp", "Instana connection success event sent: $successData")
        }
    }
}
