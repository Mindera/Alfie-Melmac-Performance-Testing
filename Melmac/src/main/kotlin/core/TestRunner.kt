package core

import android.AndroidTestRunner
import config.Config
import ios.IosTestRunner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TestRunner : KoinComponent {
    private val iosTestRunner: IosTestRunner by inject()
    private val androidTestRunner: AndroidTestRunner by inject()

    fun run(platform: String) {
        val config = try {
            Config.getPlatformConfig(platform)
        } catch (e: IllegalArgumentException) {
            println("Invalid platform selected: $platform")
            return
        }

        when (platform) {
            "ios" -> iosTestRunner.run(config)
            "android" -> androidTestRunner.run(config)
            else -> println("Unknown platform: $platform")
        }
    }
}