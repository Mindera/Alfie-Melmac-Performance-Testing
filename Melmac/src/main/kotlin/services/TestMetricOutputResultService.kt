package services

import domain.TestMetricOutputResult
import dtos.TestMetricOutputResultResponseDTO
import repos.IRepos.ITestMetricOutputResultRepository
import services.IServices.ITestMetricOutputResultService

/**
 * Service implementation for managing test metric output results.
 * Provides methods to retrieve output results for test executions.
 *
 * @property repo Repository for TestMetricOutputResult entities.
 */
class TestMetricOutputResultService(
    private val repo: ITestMetricOutputResultRepository
) : ITestMetricOutputResultService {

    /**
     * Retrieves all test metric output results.
     *
     * @return List of [TestMetricOutputResultResponseDTO] representing all output results.
     */
    override suspend fun getAll(): List<TestMetricOutputResultResponseDTO> = 
        repo.findAll().map { outputResult: TestMetricOutputResult ->
            toResponse(outputResult)
        }

    /**
     * Retrieves all test metric output results for a specific test execution.
     *
     * @param testExecutionId The ID of the test execution.
     * @return List of [TestMetricOutputResultResponseDTO] for the given execution.
     */
    override suspend fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResultResponseDTO> =
        repo.getByExecutionId(testExecutionId).map { outputResult: TestMetricOutputResult ->
            toResponse(outputResult)
        }

    /**
     * Converts a [TestMetricOutputResult] entity to a [TestMetricOutputResultResponseDTO].
     *
     * @param outputResult The entity to convert.
     * @return The corresponding DTO.
     * @throws IllegalArgumentException if the entity's ID is null.
     */
    private fun toResponse(outputResult: TestMetricOutputResult): TestMetricOutputResultResponseDTO {
        return TestMetricOutputResultResponseDTO(
            testMetricOutputResultId = outputResult.testMetricOutputResultId ?: throw IllegalArgumentException("testMetricOutputResultId is null"),
            value = outputResult.value,
            metricOutputMetricOutputId = outputResult.metricOutputMetricOutputId,
            testExecutionTestExecutionId = outputResult.testExecutionTestExecutionId
        )
    }
}