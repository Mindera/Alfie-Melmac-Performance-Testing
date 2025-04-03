package ios

/**
 * Object responsible for executing XCUITest commands.
 * Provides functionality to run UI tests on iOS simulators using `xcodebuild`.
 */
object XCUITestCommands {

    /**
     * Runs an XCUITest with the specified target resource ID.
     *
     * @param targetResourceId The resource ID of the target UI element to locate during the test.
     * This value is passed as an environment variable to the test process.
     * @return `true` if the test passes successfully, `false` otherwise.
     */
    fun runXCUITest(targetResourceId: String): Boolean {
        println("Running XCUITest with target resource ID: $targetResourceId")
        val process = ProcessBuilder(
            "xcodebuild",
            "-scheme", "YourAppUITests",
            "-destination", "platform=iOS Simulator,name=iPhone 16 Pro",
            "test"
        )
        process.environment()["TARGET_RESOURCE_ID"] = targetResourceId
        val startedProcess = process.start()
        startedProcess.waitFor()

        val output = startedProcess.inputStream.bufferedReader().readText()
        println("XCUITest Output:\n$output")

        return startedProcess.exitValue() == 0
    }
}