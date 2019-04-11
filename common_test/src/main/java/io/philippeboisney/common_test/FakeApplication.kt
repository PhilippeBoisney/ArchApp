package io.philippeboisney.common_test

import android.app.Application
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module

/**
 * We use a separate [Application] for tests to prevent initializing release modules.
 * On the contrary, we will provide inside our tests custom [Module] directly.
 */
class FakeApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf())
    }
}