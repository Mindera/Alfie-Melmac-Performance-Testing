package core

import android.AndroidAppManager
import android.AndroidDeviceManager
import android.AndroidTestRunner
import ios.IosAppManager
import ios.IosDeviceManager
import ios.IosTestRunner
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // Android dependencies
    single<DeviceManager>(qualifier = named("android")) { AndroidDeviceManager } // Bind AndroidDeviceManager with a qualifier
    single<AppManager>(qualifier = named("android")) { AndroidAppManager }
    single { AndroidTestRunner() }

    // iOS dependencies
    single<DeviceManager>(qualifier = named("ios")) { IosDeviceManager } // Bind IosDeviceManager with a qualifier
    single<AppManager>(qualifier = named("ios")) { IosAppManager }
    single { IosTestRunner() }
}