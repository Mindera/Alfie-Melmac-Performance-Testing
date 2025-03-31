package android

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

object AndroidTestCommands {
    /**
     * Measures the time taken to launch the app and locate a specific UI element.
     *
     * @param packageName The package name of the app.
     * @param mainActivity The main activity of the app.
     * @param resourceId The resource ID of the target UI element.
     * @return True if the element is found, false otherwise.
     */
    fun measureAppStartupTime(
            packageName: String,
            mainActivity: String,
            resourceId: String
    ): Boolean {
        try {
            println("🚀 Launching Android app: $packageName/$mainActivity")

            // Force stop the app to ensure a clean state
            ProcessBuilder("adb", "shell", "am", "force-stop", packageName).start().waitFor()

            // Record the start time
            val startTime = System.currentTimeMillis()

            // Launch the app
            val launchProcess =
                    ProcessBuilder(
                                    "adb",
                                    "shell",
                                    "am",
                                    "start",
                                    "-n",
                                    "$packageName/$mainActivity"
                            )
                            .start()
            launchProcess.waitFor()
            if (launchProcess.exitValue() != 0) {
                throw RuntimeException("Failed to launch Android app.")
            }
            println("✅ Android app launched successfully.")

            // Search for the target UI element
            println("🔍 Searching for UI element: $resourceId")
            val elementFoundTime = searchForElement(resourceId)

            // Calculate the total time taken
            val totalTimeTaken = elementFoundTime - startTime

            println("Start time: $startTime")
            println("⏱️ Time taken to find element: ${elementFoundTime - startTime} ms")
            println("⏱️ Total time taken: $totalTimeTaken ms")
            return true
        } catch (e: Exception) {
            println("❌ Error during app startup time measurement: ${e.message}")
            return false
        }
    }

    private fun searchForElement(resourceId: String, timeoutInSeconds: Int = 60): Long {
        val timeoutInMillis = timeoutInSeconds * 1000
        val elementFound = AtomicBoolean(false)
        var elementFoundTime: Long = -1

        // Directory to store dump files
        val dumpDirectory = File("ui_dumps")
        if (!dumpDirectory.exists()) {
            dumpDirectory.mkdirs()
            println("✅ Created dump directory: ${dumpDirectory.absolutePath}")
        }

        // Create a subfolder for this execution
        val executionSubfolder = File(dumpDirectory, "execution_${System.currentTimeMillis()}")
        if (!executionSubfolder.exists()) {
            executionSubfolder.mkdirs()
            println("✅ Created execution subfolder: ${executionSubfolder.absolutePath}")
        }

        // Shared queue for dump files
        val dumpQueue = LinkedBlockingQueue<File>(10)

        // Executors for dump generation and analysis
        val dumpExecutor = Executors.newFixedThreadPool(2)
        val analysisExecutor = Executors.newFixedThreadPool(2)

        // Submit dump generation tasks
        repeat(2) { generatorIndex ->
            dumpExecutor.submit {
                while (!elementFound.get()) {
                    try {
                        val timestamp = System.currentTimeMillis()
                        val dumpFile = File(executionSubfolder, "ui_dump_$timestamp.xml")
                        println(
                                "📝 [Generator $generatorIndex] Generating dump file: ${dumpFile.absolutePath}"
                        )
                        val dumpProcess =
                                ProcessBuilder(
                                                "adb",
                                                "shell",
                                                "uiautomator",
                                                "dump",
                                                "/sdcard/ui_dump.xml"
                                        )
                                        .start()
                        dumpProcess.waitFor()
                        if (dumpProcess.exitValue() == 0) {
                            val pullProcess =
                                    ProcessBuilder(
                                                    "adb",
                                                    "pull",
                                                    "/sdcard/ui_dump.xml",
                                                    dumpFile.absolutePath
                                            )
                                            .start()
                            pullProcess.waitFor()
                            if (pullProcess.exitValue() == 0) {
                                println(
                                        "✅ [Generator $generatorIndex] Dump file pulled: ${dumpFile.absolutePath}"
                                )
                                dumpQueue.put(dumpFile)
                            }
                        }
                    } catch (e: Exception) {
                        println(
                                "❌ [Generator $generatorIndex] Error during dump generation: ${e.message}"
                        )
                    }
                }
            }
        }

        // Submit dump analysis tasks
        repeat(2) { analyzerIndex ->
            analysisExecutor.submit {
                while (!elementFound.get()) {
                    try {
                        val dumpFile = dumpQueue.poll(100, TimeUnit.MILLISECONDS)
                        if (dumpFile != null && dumpFile.exists()) {
                            println(
                                    "🔍 [Analyzer $analyzerIndex] Analyzing dump file: ${dumpFile.absolutePath}"
                            )
                            val uiXmlContent = dumpFile.readText()
                            val elementRegex =
                                    Regex(
                                            """<.*?resource-id="$resourceId".*?clickable="true".*?enabled="true".*?bounds=.*?>"""
                                    )
                            if (elementRegex.containsMatchIn(uiXmlContent)) {
                                println(
                                        "✅ [Analyzer $analyzerIndex] UI element '$resourceId' is visible and interactable!"
                                )
                                elementFound.set(true)
                                elementFoundTime =
                                        System.currentTimeMillis() // Record the actual time the
                                // element is found
                            }
                        }
                    } catch (e: Exception) {
                        println(
                                "❌ [Analyzer $analyzerIndex] Error during dump analysis: ${e.message}"
                        )
                    }
                }
            }
        }

        // Wait for the timeout or until the element is found
        dumpExecutor.shutdown()
        analysisExecutor.shutdown()
        dumpExecutor.awaitTermination(timeoutInMillis.toLong(), TimeUnit.MILLISECONDS)
        analysisExecutor.awaitTermination(timeoutInMillis.toLong(), TimeUnit.MILLISECONDS)

        if (elementFound.get()) {
            println("✅ UI element '$resourceId' found. Timestamp: $elementFoundTime")
            return elementFoundTime
        } else {
            throw RuntimeException(
                    "❌ Timeout! UI element '$resourceId' not found or not interactable within $timeoutInSeconds seconds."
            )
        }
    }
}
