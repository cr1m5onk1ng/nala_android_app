package com.example.nala.services.workers

interface BaseWorkerManager {
    fun schedule(schedule: WorkerSchedule)

    fun cancel(workerName: String)

    suspend fun executeTask()

}