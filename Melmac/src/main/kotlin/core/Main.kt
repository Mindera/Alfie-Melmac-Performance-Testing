package core

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.util.Scanner

fun main() {
    // Start Koin with the app module
    startKoin {
        modules(appModule)
    }

    try {
        val scanner = Scanner(System.`in`)
        println("Select platform (ios/android):")
        val platform = if (scanner.hasNextLine()) {
            scanner.nextLine().lowercase().ifEmpty {
                println("No input provided. Defaulting to 'android'.")
                "android"
            }
        } else {
            println("No input detected. Defaulting to 'android'.")
            "android"
        }

        TestRunner.run(platform)
    } finally {
        stopKoin()
    }
}