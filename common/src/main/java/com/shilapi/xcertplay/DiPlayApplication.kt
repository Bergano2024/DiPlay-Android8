package com.shilapi.xcertplay

import android.app.Application
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

class DiPlayApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val writer = StringWriter()
                throwable.printStackTrace(PrintWriter(writer))

                val report = buildString {
                    appendLine("DiPlay crash report")
                    appendLine("===================")
                    appendLine("Time: ${Date()}")
                    appendLine("Thread: ${thread.name}")
                    appendLine("Android: ${Build.VERSION.RELEASE}")
                    appendLine("SDK: ${Build.VERSION.SDK_INT}")
                    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                    appendLine("Product: ${Build.PRODUCT}")
                    appendLine("Hardware: ${Build.HARDWARE}")
                    appendLine("Fingerprint: ${Build.FINGERPRINT}")
                    appendLine()
                    appendLine("Exception:")
                    appendLine(writer.toString())
                }

                File(filesDir, "last-crash.txt").writeText(report)
            } catch (_: Throwable) {
                // Never interfere with Android's normal crash handling.
            }

            if (previousHandler != null) {
                previousHandler.uncaughtException(thread, throwable)
            } else {
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }
}
