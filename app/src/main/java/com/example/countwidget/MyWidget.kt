package com.example.countwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MyWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d("MyWidget", "onUpdate called")
        try {
            WorkManager.initialize(
                context,
                Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.INFO)
                    .build()
            )
        } catch (e: IllegalStateException) {
            // WorkManager ya está inicializado
        }

        val trackingWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<TrackingWorker>(10, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "trackingWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            trackingWorkRequest as PeriodicWorkRequest
        )
        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val count = prefs.getInt("workManagerCount", 0) + 1
        prefs.edit().putInt("workManagerCount", count).apply()

        // Actualizar todos los widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val action = intent!!.action ?: ""

        if (context != null && action == "increase") {
            val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            prefs.edit().putString(
                "widgetText",
                ((prefs.getString("widgetText", "0") ?: "0").toInt() + 1).toString()
            ).apply()

            updateWidgets(context)
        }

    }

    override fun onEnabled(context: Context) {
        Log.d("MyWidget", "Widget enabled")
    }

    override fun onDisabled(context: Context) {
        Log.d("MyWidget", "Widget disabled")
    }

    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))

        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val count = prefs.getInt("workManagerCount", 0)

        ids.forEach { id -> updateAppWidget(context, manager, id, count)  }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            count: Int = 0
        ) {
            val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            val widgetText = prefs.getString("widgetText", "0")


            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.my_widget)
            views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setTextViewText(R.id.work_manager_count, "Count: $count")

            views.setOnClickPendingIntent(R.id.button, pendingIntent(context, "increase"))
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun pendingIntent(
            context: Context?,
            action: String
        ): PendingIntent? {
            val intent = Intent(context, MyWidget::class.java)
            intent.action = action

            return PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
    }
}
