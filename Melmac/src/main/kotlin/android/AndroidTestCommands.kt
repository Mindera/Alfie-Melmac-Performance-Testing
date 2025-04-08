package android

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Utility object for executing Android test commands using ADB.
 */
object AndroidTestCommands {

    /**
     * Measures the time taken to launch the app and locate a specific UI element.
     *
     * @param packageName The package name of the app.
     * @param mainActivity The main activity of the app.
     * @param resourceId The resource ID of the target UI element.
     * @return `true` if the element is found, `false` otherwise.
     */
    fun measureAppStartupTime(packageName: String, mainActivity: String, resourceId: String): Boolean {
        try {
            println("🚀 Launching Android app: $packageName/$mainActivity")

            // Ensure a clean app state
            forceStopApp(packageName)

            // Record the start time
            val startTime = System.currentTimeMillis()

            // Launch the app
            launchApp(packageName, mainActivity)

            // Search for the target UI element
            println("🔍 Searching for UI element: $resourceId")
            val elementFoundTime = searchForElement(resourceId)

            // Calculate and log the total time taken
            logStartupTime(startTime, elementFoundTime)
            return true
        } catch (e: Exception) {
            println("❌ Error during app startup time measurement: ${e.message}")
            return false
        }
    }

    /**
     * Force stops the specified app.
     *
     * @param packageName The package name of the app to stop.
     * @throws RuntimeException if the force-stop command fails.
     */
    private fun forceStopApp(packageName: String) {
        println("🛑 Force stopping app: $packageName")
        val process = ProcessBuilder("adb", "shell", "am", "force-stop", packageName).start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to force stop Android app.")
        }
    }

    /**
     * Launches the specified app.
     *
     * @param packageName The package name of the app to launch.
     * @param mainActivity The main activity of the app to launch.
     * @throws RuntimeException if the launch command fails.
     */
    private fun launchApp(packageName: String, mainActivity: String) {
        println("🚀 Launching app: $packageName/$mainActivity")
        val process = ProcessBuilder("adb", "shell", "am", "start", "-n", "$packageName/$mainActivity").start()
        process.waitFor()
        if (process.exitValue() != 0) {
            throw RuntimeException("Failed to launch Android app.")
        }
        println("✅ Android app launched successfully.")
    }

    /**
     * Logs the time taken to find the target UI element.
     *
     * @param startTime The timestamp when the app was launched.
     * @param elementFoundTime The timestamp when the target UI element was found.
     */
    private fun logStartupTime(startTime: Long, elementFoundTime: Long) {
        val totalTimeTaken = elementFoundTime - startTime
        println("Start time: $startTime")
        println("⏱️ Time taken to find element: ${elementFoundTime - startTime} ms")
        println("⏱️ Total time taken: $totalTimeTaken ms")
    }

    /**
     * Searches for a specific UI element within a timeout period.
     *
     * @param resourceId The resource ID of the target UI element.
     * @param timeoutInSeconds The maximum time to wait for the element, in seconds.
     * @return The timestamp when the element was found.
     * @throws RuntimeException if the element is not found within the timeout period.
     */
    private fun searchForElement(resourceId: String, timeoutInSeconds: Int = 60): Long {
        val timeoutInMillis = timeoutInSeconds.toLong() * 1000
        val elementFound = AtomicBoolean(false)
        var elementFoundTime: Long = -1

        val executionSubfolder = prepareDumpDirectories()

        val dumpQueue = LinkedBlockingQueue<File>(10)
        val dumpExecutor = Executors.newFixedThreadPool(2)
        val analysisExecutor = Executors.newFixedThreadPool(2)

        submitDumpGenerationTasks(dumpExecutor, executionSubfolder, dumpQueue, elementFound)
        submitDumpAnalysisTasks(analysisExecutor, dumpQueue, resourceId, elementFound) { foundTime ->
            elementFoundTime = foundTime
        }

        waitForCompletion(dumpExecutor, analysisExecutor, timeoutInMillis)

        if (elementFound.get()) {
            println("✅ UI element '$resourceId' found. Timestamp: $elementFoundTime")
            return elementFoundTime
        } else {
            throw RuntimeException("❌ Timeout! UI element '$resourceId' not found or not interactable within $timeoutInSeconds seconds.")
        }
    }

    /**
     * Prepares directories for storing UI dump files.
     *
     * @return The subfolder where dump files will be stored.
     */
    private fun prepareDumpDirectories(): File {
        val dumpDirectory = File("ui_dumps")
        if (!dumpDirectory.exists()) {
            dumpDirectory.mkdirs()
            println("✅ Created dump directory: ${dumpDirectory.absolutePath}")
        }

        val executionSubfolder = File(dumpDirectory, "execution_${System.currentTimeMillis()}")
        if (!executionSubfolder.exists()) {
            executionSubfolder.mkdirs()
            println("✅ Created execution subfolder: ${executionSubfolder.absolutePath}")
        }

        return executionSubfolder
    }

    /**
     * Submits tasks for generating UI dump files.
     *
     * @param dumpExecutor The executor service for dump generation tasks.
     * @param executionSubfolder The folder where dump files will be stored.
     * @param dumpQueue The queue to store generated dump files.
     * @param elementFound The atomic flag indicating whether the element has been found.
     */
    private fun submitDumpGenerationTasks(
        dumpExecutor: java.util.concurrent.ExecutorService,
        executionSubfolder: File,
        dumpQueue: LinkedBlockingQueue<File>,
        elementFound: AtomicBoolean
    ) {
        repeat(2) { generatorIndex ->
            dumpExecutor.submit {
                while (!elementFound.get()) {
                    try {
                        val dumpFile = generateUiDump(generatorIndex, executionSubfolder)
                        if (dumpFile != null) {
                            dumpQueue.put(dumpFile)
                        }
                    } catch (e: Exception) {
                        println("❌ [Generator $generatorIndex] Error during dump generation: ${e.message}")
                    }
                }
            }
        }
    }

    /**
     * Generates a UI dump file and pulls it from the device.
     *
     * @param generatorIndex The index of the dump generator task.
     * @param executionSubfolder The folder where the dump file will be stored.
     * @return The generated dump file, or `null` if the operation fails.
     */
    private fun generateUiDump(generatorIndex: Int, executionSubfolder: File): File? {
        val timestamp = System.currentTimeMillis()
        val dumpFile = File(executionSubfolder, "ui_dump_$timestamp.xml")
        println("📝 [Generator $generatorIndex] Generating dump file: ${dumpFile.absolutePath}")

        val dumpProcess = ProcessBuilder("adb", "shell", "uiautomator", "dump", "/sdcard/ui_dump.xml").start()
        dumpProcess.waitFor()
        if (dumpProcess.exitValue() == 0) {
            val pullProcess = ProcessBuilder("adb", "pull", "/sdcard/ui_dump.xml", dumpFile.absolutePath).start()
            pullProcess.waitFor()
            if (pullProcess.exitValue() == 0) {
                println("✅ [Generator $generatorIndex] Dump file pulled: ${dumpFile.absolutePath}")
                return dumpFile
            }
        }
        return null
    }

    /**
     * Submits tasks for analyzing UI dump files.
     *
     * @param analysisExecutor The executor service for dump analysis tasks.
     * @param dumpQueue The queue containing dump files to analyze.
     * @param resourceId The resource ID of the target UI element.
     * @param elementFound The atomic flag indicating whether the element has been found.
     * @param onElementFound Callback invoked when the element is found.
     */
    private fun submitDumpAnalysisTasks(
        analysisExecutor: java.util.concurrent.ExecutorService,
        dumpQueue: LinkedBlockingQueue<File>,
        resourceId: String,
        elementFound: AtomicBoolean,
        onElementFound: (Long) -> Unit
    ) {
        repeat(2) { analyzerIndex ->
            analysisExecutor.submit {
                while (!elementFound.get()) {
                    try {
                        val dumpFile = dumpQueue.poll(100, TimeUnit.MILLISECONDS)
                        if (dumpFile != null && dumpFile.exists()) {
                            analyzeDumpFile(analyzerIndex, dumpFile, resourceId, elementFound, onElementFound)
                        }
                    } catch (e: Exception) {
                        println("❌ [Analyzer $analyzerIndex] Error during dump analysis: ${e.message}")
                    }
                }
            }
        }
    }

    /**
     * Analyzes a UI dump file to locate the target UI element.
     *
     * @param analyzerIndex The index of the dump analyzer task.
     * @param dumpFile The dump file to analyze.
     * @param resourceId The resource ID of the target UI element.
     * @param elementFound The atomic flag indicating whether the element has been found.
     * @param onElementFound Callback invoked when the element is found.
     */
    private fun analyzeDumpFile(
        analyzerIndex: Int,
        dumpFile: File,
        resourceId: String,
        elementFound: AtomicBoolean,
        onElementFound: (Long) -> Unit
    ) {
        println("🔍 [Analyzer $analyzerIndex] Analyzing dump file: ${dumpFile.absolutePath}")
        val uiXmlContent = dumpFile.readText()
        val elementRegex = Regex("""<.*?resource-id="$resourceId".*?>""")
        if (elementRegex.containsMatchIn(uiXmlContent)) {
            println("✅ [Analyzer $analyzerIndex] UI element '$resourceId' is visible and interactable!")
            elementFound.set(true)
            onElementFound(System.currentTimeMillis())
        }
    }

    /**
     * Waits for the completion of dump generation and analysis tasks.
     *
     * @param dumpExecutor The executor service for dump generation tasks.
     * @param analysisExecutor The executor service for dump analysis tasks.
     * @param timeoutInMillis The maximum time to wait for task completion, in milliseconds.
     */
    private fun waitForCompletion(
        dumpExecutor: java.util.concurrent.ExecutorService,
        analysisExecutor: java.util.concurrent.ExecutorService,
        timeoutInMillis: Long
    ) {
        dumpExecutor.shutdown()
        analysisExecutor.shutdown()
        dumpExecutor.awaitTermination(timeoutInMillis, TimeUnit.MILLISECONDS)
        analysisExecutor.awaitTermination(timeoutInMillis, TimeUnit.MILLISECONDS)
    }
}