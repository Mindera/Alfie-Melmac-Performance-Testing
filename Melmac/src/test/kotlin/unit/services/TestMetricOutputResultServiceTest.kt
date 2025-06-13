import io.mockk.*
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.ITestMetricOutputResultRepository
import services.IServices.ITestMetricOutputResultService
import services.TestMetricOutputResultService

class TestMetricOutputResultServiceTest {

    private val repo = mockk<repos.IRepos.ITestMetricOutputResultRepository>()
    private val mapper = mockk<TestMetricOutputResultMapper>()
    private val service = TestMetricOutputResultService(repo, mapper)

    @Test
    fun `getAll returns mapped DTOs`() {
        // Arrange
        val entity = domain.TestMetricOutputResult(
            testMetricOutputResultId = 1,
            value = "val",
            metricOutputMetricOutputId = 3,
            testExecutionTestExecutionId = 2
        )
        val dto = dtos.TestMetricOutputResultResponseDTO(
            testMetricOutputResultId = 1,
            value = "val",
            metricOutputMetricOutputId = 3,
            testExecutionTestExecutionId = 2
        )
        coEvery { repo.findAll() } returns listOf(entity)
        coEvery { mapper.toDto(entity) } returns dto

        // Act
        val result = service.getAll()

        // Assert
        assertEquals(listOf(dto), result)
        coVerify { repo.findAll() }
        coVerify { mapper.toDto(entity) }
    }

    @Test
    fun `getByExecutionId returns mapped DTOs`() {
        // Arrange
        val entity = domain.TestMetricOutputResult(
            testMetricOutputResultId = 1,
            value = "val",
            metricOutputMetricOutputId = 3,
            testExecutionTestExecutionId = 2
        )
        val dto = dtos.TestMetricOutputResultResponseDTO(
            testMetricOutputResultId = 1,
            value = "val",
            metricOutputMetricOutputId = 3,
            testExecutionTestExecutionId = 2
        )
        coEvery { repo.getByExecutionId(2) } returns listOf(entity)
        coEvery { mapper.toDto(entity) } returns dto

        // Act
        val result = service.getByExecutionId(2)

        // Assert
        assertEquals(listOf(dto), result)
        coVerify { repo.getByExecutionId(2) }
        coVerify { mapper.toDto(entity) }
    }
}