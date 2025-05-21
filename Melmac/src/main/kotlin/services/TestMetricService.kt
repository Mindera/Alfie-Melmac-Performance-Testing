package services

import domain.TestMetric
import domain.dtos.TestMetricRequestDTO
import domain.dtos.TestMetricResponseDTO
import repos.IRepos.ITestMetricRepository
import services.IServices.ITestMetricService

class TestMetricService(
    private val repo: ITestMetricRepository
) : ITestMetricService {

    override suspend fun getAll(): List<TestMetricResponseDTO> =
        repo.getAll().map { it.toResponse() }

    override suspend fun getByExecutionId(testExecutionId: Int): List<TestMetricResponseDTO> =
        repo.getByExecutionId(testExecutionId).map { it.toResponse() }

    override suspend fun create(dto: TestMetricRequestDTO): TestMetricResponseDTO {
        val created = repo.create(
            TestMetric(
                testExecutionId = dto.testExecutionId,
                outputId = dto.outputId,
                value = dto.value
            )
        )
        return created.toResponse()
    }

    override suspend fun delete(id: Int) {
        repo.deleteById(id)
    }

    private fun TestMetric.toResponse() = TestMetricResponseDTO(
        id = this.id!!,
        testExecutionId = this.testExecutionId,
        outputId = this.outputId,
        value = this.value
    )
}
