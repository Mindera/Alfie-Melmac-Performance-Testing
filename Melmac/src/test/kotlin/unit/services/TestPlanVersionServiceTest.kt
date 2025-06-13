package unit.TestPlanVersion

import domain.*
import dtos.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*

import services.TestPlanVersionService
import java.time.LocalDateTime

class TestPlanVersionServiceTest {

    private val testPlanVersionRepository = mockk<ITestPlanVersionRepository>()
    private val testThresholdRepository = mockk<IThresholdRepository>()
    private val testPlanMetricParameterRepository = mockk<ITestPlanMetricParameterValueRepository>()
    private val testPlanExecutionTypeParameterRepository = mockk<ITestPlanExecutionTypeParameterValueRepository>()
    private val testSuiteVersionPlanRepository = mockk<ITestSuiteVersionPlanRepository>()
    private val testPlanVersionMapper = mockk<TestPlanVersionMapper>()
    private val testThresholdMapper = mockk<TestThresholdMapper>()
    private val testMetricParameterMapper = mockk<TestMetricParameterMapper>()
    private val testExecutionTypeParameterMapper = mockk<TestExecutionTypeParameterMapper>()

    private val service = TestPlanVersionService(
        testPlanVersionRepository,
        testThresholdRepository,
        testPlanMetricParameterRepository,
        testPlanExecutionTypeParameterRepository,
        testSuiteVersionPlanRepository,
        testPlanVersionMapper,
        testThresholdMapper,
        testMetricParameterMapper,
        testExecutionTypeParameterMapper
    )

    private val testPlanVersion = TestPlanVersion(
        testPlanVersionId = 1,
        version = "1.0",
        creationTimestamp = LocalDateTime.now(),
        notes = "notes",
        appPackage = "pkg",
        mainActivity = "main",
        testPlanTestPlanId = 1,
        deviceDeviceId = 1,
        appVersionAppVersionId = 1,
        executionTypeExecutionTypeId = 1
    )

    private val threshold = TestThreshold(
        testThresholdId = 1,
        targetValue = 100,
        thresholdTypeThresholdTypeId = 2,
        testPlanVersionTestPlanVersionId = 1,
        metricOutputMetricOutputId = 1
    )

    private val metricParameter = TestMetricParameter(
        testMetricParameterId = 1,
        parameterValue = "val",
        testPlanVersionTestPlanVersionId = 1,
        metricParameterMetricParameterId = 1
    )

    private val executionTypeParameter = TestExecutionTypeParameter(
        testExecutionTypeParameterId = 1,
        parameterValue = "val",
        testPlanVersionTestPlanVersionId = 1,
        executionTypeParameterExecutionTypeParameterId = 1
    )

    private val testSuiteVersionPlan = TestSuiteVersionPlan(
        testSuiteVersionTestSuiteVersionId = 10,
        testPlanVersionTestPlanVersionId = 1,
        order = 1
    )

    private val thresholdDTO = TestThresholdResponseDTO(
        testThresholdId = 1,
        targetValue = 100,
        thresholdTypeThresholdTypeId = 2,
        testPlanVersionTestPlanVersionId = 1,
        metricOutputMetricOutputId = 1
    )

    private val metricParameterDTO = TestMetricParameterResponseDTO(
        testMetricParameterId = 1,
        parameterValue = "val",
        testPlanVersionTestPlanVersionId = 1,
        metricParameterMetricParameterId = 1
    )

    private val executionTypeParameterDTO = TestExecutionTypeParameterResponseDTO(
        testExecutionTypeParameterId = 1,
        parameterValue = "val",
        testPlanVersionTestPlanVersionId = 1,
        executionTypeParameterExecutionTypeParameterId = 1
    )

    private val testPlanVersionResponseDTO = mockk<TestPlanVersionResponseDTO>()

    @Test
    fun `getTestPlanVersionsByTestPlanId returns mapped DTOs`() {
        // Arrange
        every { testPlanVersionRepository.findByTestPlanId(1) } returns listOf(testPlanVersion)
        every { testThresholdRepository.findByTestPlanVersionId(1) } returns listOf(threshold)
        every { testThresholdMapper.toDto(threshold) } returns thresholdDTO
        every { testPlanMetricParameterRepository.findByTestPlanVersionId(1) } returns listOf(metricParameter)
        every { testMetricParameterMapper.toDto(metricParameter) } returns metricParameterDTO
        every { testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(1) } returns listOf(executionTypeParameter)
        every { testExecutionTypeParameterMapper.toDto(executionTypeParameter) } returns executionTypeParameterDTO
        every { testSuiteVersionPlanRepository.findByTestPlanVersionId(1) } returns listOf(testSuiteVersionPlan)
        every {
            testPlanVersionMapper.toDto(
                testPlanVersion,
                listOf(thresholdDTO),
                listOf(metricParameterDTO),
                listOf(executionTypeParameterDTO),
                10
            )
        } returns testPlanVersionResponseDTO

        // Act
        val result = service.getTestPlanVersionsByTestPlanId(1)

        // Assert
        assertEquals(1, result.size)
        assertEquals(testPlanVersionResponseDTO, result[0])
        verify(exactly = 1) { testPlanVersionRepository.findByTestPlanId(1) }
        verify(exactly = 1) { testThresholdRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testThresholdMapper.toDto(threshold) }
        verify(exactly = 1) { testPlanMetricParameterRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testMetricParameterMapper.toDto(metricParameter) }
        verify(exactly = 1) { testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testExecutionTypeParameterMapper.toDto(executionTypeParameter) }
        verify(exactly = 1) { testSuiteVersionPlanRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) {
            testPlanVersionMapper.toDto(
                testPlanVersion,
                listOf(thresholdDTO),
                listOf(metricParameterDTO),
                listOf(executionTypeParameterDTO),
                10
            )
        }
    }

    @Test
    fun `getLatestTestPlanVersionByTestPlanId returns mapped DTO when found`() {
        // Arrange
        every { testPlanVersionRepository.findLatestVersionByTestPlanId(1) } returns testPlanVersion
        every { testThresholdRepository.findByTestPlanVersionId(1) } returns listOf(threshold)
        every { testThresholdMapper.toDto(threshold) } returns thresholdDTO
        every { testPlanMetricParameterRepository.findByTestPlanVersionId(1) } returns listOf(metricParameter)
        every { testMetricParameterMapper.toDto(metricParameter) } returns metricParameterDTO
        every { testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(1) } returns listOf(executionTypeParameter)
        every { testExecutionTypeParameterMapper.toDto(executionTypeParameter) } returns executionTypeParameterDTO
        every { testSuiteVersionPlanRepository.findByTestPlanVersionId(1) } returns listOf(testSuiteVersionPlan)
        every {
            testPlanVersionMapper.toDto(
                testPlanVersion,
                listOf(thresholdDTO),
                listOf(metricParameterDTO),
                listOf(executionTypeParameterDTO),
                10
            )
        } returns testPlanVersionResponseDTO

        // Act
        val result = service.getLatestTestPlanVersionByTestPlanId(1)

        // Assert
        assertEquals(testPlanVersionResponseDTO, result)
        verify(exactly = 1) { testPlanVersionRepository.findLatestVersionByTestPlanId(1) }
        verify(exactly = 1) { testThresholdRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testThresholdMapper.toDto(threshold) }
        verify(exactly = 1) { testPlanMetricParameterRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testMetricParameterMapper.toDto(metricParameter) }
        verify(exactly = 1) { testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testExecutionTypeParameterMapper.toDto(executionTypeParameter) }
        verify(exactly = 1) { testSuiteVersionPlanRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) {
            testPlanVersionMapper.toDto(
                testPlanVersion,
                listOf(thresholdDTO),
                listOf(metricParameterDTO),
                listOf(executionTypeParameterDTO),
                10
            )
        }
    }

    @Test
    fun `getLatestTestPlanVersionByTestPlanId returns null when not found`() {
        // Arrange
        every { testPlanVersionRepository.findLatestVersionByTestPlanId(1) } returns null

        // Act
        val result = service.getLatestTestPlanVersionByTestPlanId(1)

        // Assert
        assertNull(result)
        verify(exactly = 1) { testPlanVersionRepository.findLatestVersionByTestPlanId(1) }
    }

    @Test
    fun `getTestPlanVersionById returns mapped DTO when found`() {
        // Arrange
        every { testPlanVersionRepository.findById(1) } returns testPlanVersion
        every { testThresholdRepository.findByTestPlanVersionId(1) } returns listOf(threshold)
        every { testThresholdMapper.toDto(threshold) } returns thresholdDTO
        every { testPlanMetricParameterRepository.findByTestPlanVersionId(1) } returns listOf(metricParameter)
        every { testMetricParameterMapper.toDto(metricParameter) } returns metricParameterDTO
        every { testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(1) } returns listOf(executionTypeParameter)
        every { testExecutionTypeParameterMapper.toDto(executionTypeParameter) } returns executionTypeParameterDTO
        every { testSuiteVersionPlanRepository.findByTestPlanVersionId(1) } returns listOf(testSuiteVersionPlan)
        every {
            testPlanVersionMapper.toDto(
                testPlanVersion,
                listOf(thresholdDTO),
                listOf(metricParameterDTO),
                listOf(executionTypeParameterDTO),
                10
            )
        } returns testPlanVersionResponseDTO

        // Act
        val result = service.getTestPlanVersionById(1)

        // Assert
        assertEquals(testPlanVersionResponseDTO, result)
        verify(exactly = 1) { testPlanVersionRepository.findById(1) }
        verify(exactly = 1) { testThresholdRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testThresholdMapper.toDto(threshold) }
        verify(exactly = 1) { testPlanMetricParameterRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testMetricParameterMapper.toDto(metricParameter) }
        verify(exactly = 1) { testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) { testExecutionTypeParameterMapper.toDto(executionTypeParameter) }
        verify(exactly = 1) { testSuiteVersionPlanRepository.findByTestPlanVersionId(1) }
        verify(exactly = 1) {
            testPlanVersionMapper.toDto(
                testPlanVersion,
                listOf(thresholdDTO),
                listOf(metricParameterDTO),
                listOf(executionTypeParameterDTO),
                10
            )
        }
    }

    @Test
    fun `getTestPlanVersionById returns null when not found`() {
        // Arrange
        every { testPlanVersionRepository.findById(1) } returns null

        // Act
        val result = service.getTestPlanVersionById(1)

        // Assert
        assertNull(result)
        verify(exactly = 1) { testPlanVersionRepository.findById(1) }
    }
}