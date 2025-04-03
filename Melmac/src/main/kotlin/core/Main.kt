package core

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.util.Scanner

/**
 * Entry point of the application.
 * Initializes the dependency injection framework (Koin), prompts the user to select a platform,
 * and runs the appropriate test runner based on the selected platform.
 */
fun main() {
    // Start Koin with the app module
    startKoin {
        modules(appModule)
    }

    try {
        val scanner = Scanner(System.`in`)
        println("Select platform (ios/android):")
        
        // Read user input for platform selection
        val platform = if (scanner.hasNextLine()) {
            scanner.nextLine().lowercase().ifEmpty {
                println("No input provided. Defaulting to 'android'.")
                "android"
            }
        } else {
            println("No input detected. Defaulting to 'android'.")
            "android"
        }

        // Run the test runner for the selected platform
        TestRunner.run(platform)
    } finally {
        // Stop Koin to release resources
        stopKoin()
    }
}