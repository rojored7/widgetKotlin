package com.example.countwidget

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.instana.android.Instana

class TrackingWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.d("TrackingWorker", "Periodic tracking event")

        // Incrementar el contador en SharedPreferences
        val prefs = applicationContext.getSharedPreferences(applicationContext.packageName, Context.MODE_PRIVATE)
        val count = prefs.getInt("workManagerCount", 0) + 1
        prefs.edit().putInt("workManagerCount", count).apply()

        // Enviar datos a Instana
        val trackingData = mapOf(
            "event" to "TrackingEvent",
            "description" to "Periodic tracking event",
            "count" to count.toString()
        )
        Instana.meta.putAll(trackingData)
        Log.d("TrackingWorker", "Sent tracking data to Instana: $trackingData")
        // Verificar la conectividad con Instana
        (applicationContext as MyApp).verifyInstanaConnectivity()

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
