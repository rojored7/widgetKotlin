package com.example.countwidget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.instana.android.CustomEvent
import com.instana.android.Instana
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackingWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Obtener la hora actual
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        // Enviar datos a Instana
        val trackingData = mutableMapOf<String, String>()
        trackingData["description"] = "Periodic tracking event"
        trackingData["time"] = currentTime

        // Crear el CustomEvent
        val customEvent = CustomEvent(eventName = "TrackingEvent")
        customEvent.meta = trackingData

        // Reportar el evento a Instana
        Instana.reportEvent(customEvent)

        // Log para verificar que se envió el evento
        Log.d("TrackingWorker", "Sent tracking data to Instana: $trackingData")

        // Actualizar el widget
        updateWidgets(applicationContext)

        return Result.success()
    }

    private fun updateWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, MyWidget::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
        for (appWidgetId in appWidgetIds) {
            MyWidget.updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}
