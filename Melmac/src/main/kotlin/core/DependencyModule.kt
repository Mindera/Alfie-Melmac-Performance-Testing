package core

import android.AndroidAppManager
import android.AndroidDeviceManager
import android.AndroidTestRunner
import ios.IOSAppManager
import ios.IOSDeviceManager
import ios.IOSTestRunner
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Dependency injection module for managing platform-specific dependencies.
 * Provides Android and iOS implementations for device management, app management, and test runners.
 */
val appModule = module {

    // Android dependencies
    /**
     * Provides the Android implementation of [DeviceManager].
     */
    single<DeviceManager>(qualifier = named("android")) { AndroidDeviceManager }

    /**
     * Provides the Android implementation of [AppManager].
     */
    single<AppManager>(qualifier = named("android")) { AndroidAppManager }

    /**
     * Provides the Android test runner.
     */
    single { AndroidTestRunner() }

    // iOS dependencies
    /**
     * Provides the iOS implementation of [DeviceManager].
     */
    single<DeviceManager>(qualifier = named("ios")) { IOSDeviceManager }

    /**
     * Provides the iOS implementation of [AppManager].
     */
    single<AppManager>(qualifier = named("ios")) { IOSAppManager }

    /**
     * Provides the iOS test runner.
     */
    single { IOSTestRunner() }
}