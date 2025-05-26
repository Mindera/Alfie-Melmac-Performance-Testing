package services

import domain.TestMetricOutputResult
import dtos.TestMetricOutputResultResponseDTO
import repos.IRepos.ITestMetricOutputResultRepository
import services.IServices.ITestMetricOutputResultService

class TestMetricOutputResultService(
    private val repo: ITestMetricOutputResultRepository
) : ITestMetricOutputResultService {

    override suspend fun getAll(): List<TestMetricOutputResultResponseDTO> = 
        repo.findAll().map { outputResult: TestMetricOutputResult ->
            toResponse(outputResult)
        }

    override suspend fun getByExecutionId(testExecutionId: Int): List<TestMetricOutputResultResponseDTO> =
        repo.getByExecutionId(testExecutionId).map { outputResult: TestMetricOutputResult ->
            toResponse(outputResult)
        }

    private fun toResponse(outputResult: TestMetricOutputResult): TestMetricOutputResultResponseDTO {
        return TestMetricOutputResultResponseDTO(
            testMetricOutputResultId = outputResult.testMetricOutputResultId ?: throw IllegalArgumentException("testMetricOutputResultId is null"),
            value = outputResult.value,
            metricOutputMetricOutputId = outputResult.metricOutputMetricOutputId,
            testExecutionTestExecutionId = outputResult.testExecutionTestExecutionId
        )
    }
}
