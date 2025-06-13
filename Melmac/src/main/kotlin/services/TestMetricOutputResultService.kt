package services

import domain.TestMetricOutputResult
import dtos.TestMetricOutputResultResponseDTO
import repos.IRepos.ITestMetricOutputResultRepository
import services.IServices.ITestMetricOutputResultService
import mappers.TestMetricOutputResultMapper

/**
 * Service implementation for managing test metric output results.
 * Provides methods to retrieve output results for test executions.
 *
 * @property repo Repository for TestMetricOutputResult entities.
 */
class TestMetricOutputResultService(
    private val repo: ITestMetricOutputResultRepository,
    private val testMetricOutputResultMapper: TestMetricOutputResultMapper
) : ITestMetricOutputResultService {

    /**
     * Retrieves all test metric output results.
     *
     * @return List of [TestMetricOutputResultResponseDTO] representing all output results.
     */
    override fun getAll(): List<TestMetricOutputResultResponseDTO> = 
        repo.findAll().map { outputResult: TestMetricOutputResult ->
            testMetricOutputResultMapper.toDto(outputResult)
        }

    /**
     * Retrieves all test metric output results for a specific test execution.
     *
     * @param testExecutionId The ID of the test execution.
     * @return List of [TestMetricOutputResultResponseDTO] for the given execution.
     */
    override fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResultResponseDTO> =
        repo.getByExecutionId(testExecutionId).map { outputResult: TestMetricOutputResult ->
            testMetricOutputResultMapper.toDto(outputResult)
        }
}