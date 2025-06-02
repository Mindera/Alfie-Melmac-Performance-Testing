package core.runners

import android.AndroidTestRunner
import config.Config
import dtos.TestExecutionConfigDTO
import ios.IOSTestRunner
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Object responsible for running platform-specific test runners.
 * Determines the platform (iOS or Android) and delegates the test execution to the appropriate runner.
 */
class TestRunner(
    private val iOSTestRunner: IOSTestRunner,
    private val androidTestRunner: AndroidTestRunner,
): ITestRunner {

    /**
     * Executes the test runner for the specified platform based on the configuration.
     *
     * @param config Full configuration of the test.
     * @return A map of output names to their resulting values.
     */
    override fun run(config: TestExecutionConfigDTO): Map<String?, String> {
        return when (config.platform.lowercase()) {
            "ios" -> iOSTestRunner.run(config).mapKeys { it.key }
            "android" -> androidTestRunner.run(config).mapKeys { it.key }
            else -> {
                println("Unknown platform: ${config.platform}")
                emptyMap()
            }
        }
    }
}