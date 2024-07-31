package com.example.countwidget

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.instana.android.Instana

class TrackingWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        Log.d("TrackingWorker", "Periodic tracking event")


        return Result.success()
    }
}
