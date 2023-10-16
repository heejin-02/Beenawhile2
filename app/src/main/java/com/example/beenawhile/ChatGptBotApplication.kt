package com.example.beenawhile

import android.app.Application
import com.example.beenawhile.di.chatModule
import com.example.beenawhile.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ChatGptBotApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin()
    }

    private fun startKoin() {
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@ChatGptBotApplication)
            // Load modules
            modules(
                listOf(
                    networkModule,
                    chatModule
                )
            )
        }
    }
}