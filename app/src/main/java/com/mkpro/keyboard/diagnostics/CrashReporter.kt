package com.mkpro.keyboard.diagnostics

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Installs a process-wide Thread.UncaughtExceptionHandler so that whatever
 * actually crashes - KeyboardService, MainActivity, anything - posts a
 * notification with the real exception type, message, and top stack frames
 * BEFORE the system's normal "app has stopped" dialog takes over.
 *
 * Why this exists: a try/catch inside one function (e.g.
 * onCreateInputView()) only catches synchronous exceptions during that
 * call. Compose's actual first draw/layout happens later via a Choreographer
 * callback on the same thread but outside that function's stack frame, so
 * such a crash is invisible to a local try/catch. A process-wide handler
 * catches it regardless of where or when it happens on that thread.
 *
 * This does NOT stop the crash from happening - the previous default
 * handler is still invoked afterward so Android's normal crash handling
 * proceeds unchanged. It only makes the real cause visible to the person
 * running the app, without needing adb/logcat access.
 */
object CrashReporter {

    private const val CHANNEL_ID = "mkpro_crash_reports"
    private const val NOTIFICATION_ID = 9001
    private var installed = false

    fun install(context: Context) {
        if (installed) return
        installed = true

        val appContext = context.applicationContext
        createNotificationChannel(appContext)

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { postCrashNotification(appContext, throwable) }
            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Crash reports",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Shows the real exception when the app crashes, for debugging."
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun postCrashNotification(context: Context, throwable: Throwable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return // can't post without the runtime permission; nothing else we can do here
        }

        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        val fullTrace = stringWriter.toString()
        val summary = "${throwable::class.java.name}: ${throwable.message}"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Mechanical Keyboard Pro crashed")
            .setContentText(summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$summary\n\n${fullTrace.take(3000)}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        runCatching { manager.notify(NOTIFICATION_ID, notification) }
    }
}
