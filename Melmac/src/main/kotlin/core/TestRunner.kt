package core

import android.AndroidTestRunner
import config.Config
import ios.IOSTestRunner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Object responsible for running platform-specific test runners.
 * Determines the platform (iOS or Android) and delegates the test execution to the appropriate runner.
 */
object TestRunner : KoinComponent {

    // Dependency injection for the iOS test runner
    private val iOSTestRunner: IOSTestRunner by inject()

    // Dependency injection for the Android test runner
    private val androidTestRunner: AndroidTestRunner by inject()

    /**
     * Executes the test runner for the specified platform.
     *
     * @param platform The platform to run the tests on (e.g., "ios" or "android").
     * @throws IllegalArgumentException if the platform configuration is invalid or not found.
     */
    fun run(platform: String) {
        val config = try {
            // Retrieve the configuration for the specified platform
            Config.getPlatformConfig(platform)
        } catch (e: IllegalArgumentException) {
            println("Invalid platform selected: $platform")
            return
        }

        // Delegate the test execution to the appropriate platform-specific runner
        when (platform) {
            "ios" -> iOSTestRunner.run(config)
            "android" -> androidTestRunner.run(config)
            else -> println("Unknown platform: $platform")
        }
    }
}