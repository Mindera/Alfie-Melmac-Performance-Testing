package core.runners

import dtos.TestExecutionConfigDTO

interface ITestRunner {
    /**
     * Runs the test based on the given configuration.
     * @param config Full configuration of the test.
     * @return A map of output names to their resulting values.
     */
    fun run(config: TestExecutionConfigDTO): Map<String?, String>
}