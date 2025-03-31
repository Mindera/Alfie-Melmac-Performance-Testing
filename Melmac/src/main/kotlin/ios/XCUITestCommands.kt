package ios

object XCUITestCommands {
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