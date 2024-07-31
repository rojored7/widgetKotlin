package com.example.countwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.instana.android.Instana
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import android.util.Log
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class MyWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    )

    {
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


        val trackingWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<TrackingWorker>(30, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(context).enqueue(trackingWorkRequest)

        // There may be multiple widgets active, so update all of them
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
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, javaClass))

        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun pendingIntent(
        context: Context?,
        action: String
    ): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action

        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {


        val prefs = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val widgetText = prefs.getString("widgetText","0")
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.my_widget)
        views.setTextViewText(R.id.appwidget_text, widgetText)

        views.setOnClickPendingIntent(R.id.button, pendingIntent(context, "increase"))
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}