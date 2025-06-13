package unit.TestSuite

import domain.*
import dtos.*
import mappers.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*
import services.*
import services.IServices.ITestExecutionService
import java.time.LocalDateTime

class TestSuiteServiceTest {

    private val testSuiteRepository = mockk<ITestSuiteRepository>()
    private val testSuiteVersionRepository = mockk<ITestSuiteVersionRepository>()
    private val testSuiteVersionPlanRepository = mockk<ITestSuiteVersionPlanRepository>()
    private val testPlanVersionRepository = mockk<ITestPlanVersionRepository>()
    private val testSuiteExecutionRepository = mockk<ITestSuiteExecutionRepository>()
    private val testExecutionService = mockk<ITestExecutionService>()
    private val testThresholdRepository = mockk<IThresholdRepository>()
    private val testMetricParameterRepository = mockk<ITestPlanMetricParameterValueRepository>()
    private val testExecutionTypeParameterRepository = mockk<ITestPlanExecutionTypeParameterValueRepository>()
    private val testPlanVersionMapper = mockk<TestPlanVersionMapper>()
    private val testThresholdMapper = mockk<TestThresholdMapper>()
    private val testMetricParameterMapper = mockk<TestMetricParameterMapper>()
    private val testExecutionTypeParameterMapper = mockk<TestExecutionTypeParameterMapper>()

    private val testSuiteService = TestSuiteService(
        testSuiteRepository,
        testSuiteVersionRepository,
        testSuiteVersionPlanRepository,
        testPlanVersionRepository,
        testSuiteExecutionRepository,
        testExecutionService,
        testThresholdRepository,
        testMetricParameterRepository,
        testExecutionTypeParameterRepository,
        testPlanVersionMapper,
        testThresholdMapper,
        testMetricParameterMapper,
        testExecutionTypeParameterMapper
    )

    @Test
    fun `getAllTestSuites returns mapped DTOs`() {
        // Arrange
        val now = LocalDateTime.now()
        val testSuites = listOf(
            TestSuite(1, "Suite1", "desc1", now, true),
            TestSuite(2, "Suite2", null, now, false)
        )
        every { testSuiteRepository.findAll() } returns testSuites

        // Act
        val result = testSuiteService.getAllTestSuites()

        // Assert
        assertEquals(2, result.size)
        with(result[0]) {
            assertEquals(1, testSuiteId)
            assertEquals("Suite1", testSuiteName)
            assertEquals("desc1", testSuiteDescription)
            assertEquals(now, creationTimestamp)
            assertTrue(isActive)
        }
        with(result[1]) {
            assertEquals(2, testSuiteId)
            assertEquals("Suite2", testSuiteName)
            assertNull(testSuiteDescription)
            assertEquals(now, creationTimestamp)
            assertFalse(isActive)
        }
        verify(exactly = 1) { testSuiteRepository.findAll() }
    }

    @Test
    fun `getTestSuiteById returns DTO when found`() {
        // Arrange
        val now = LocalDateTime.now()
        val testSuite = TestSuite(1, "Suite1", "desc1", now, true)
        every { testSuiteRepository.findById(1) } returns testSuite

        // Act
        val result = testSuiteService.getTestSuiteById(1)

        // Assert
        assertNotNull(result)
        with(result!!) {
            assertEquals(1, testSuiteId)
            assertEquals("Suite1", testSuiteName)
            assertEquals("desc1", testSuiteDescription)
            assertEquals(now, creationTimestamp)
            assertTrue(isActive)
        }
        verify(exactly = 1) { testSuiteRepository.findById(1) }
    }

    @Test
    fun `getTestSuiteById returns null when not found`() {
        // Arrange
        every { testSuiteRepository.findById(99) } returns null

        // Act
        val result = testSuiteService.getTestSuiteById(99)

        // Assert
        assertNull(result)
        verify(exactly = 1) { testSuiteRepository.findById(99) }
    }

    @Test
    fun `createTestSuite creates and returns DTO`() {
        // Arrange
        val request = TestSuiteRequestDTO("Suite1", "desc1")
        val savedId = 1

        every { testSuiteRepository.save(any()) } returns savedId
        every { testSuiteVersionRepository.save(any()) } returns 1
        every { testSuiteRepository.findLatestVersionByTestSuiteId(savedId) } returns null

        // Act
        val result = testSuiteService.createTestSuite(request)

        // Assert
        assertEquals(savedId, result.testSuiteId)
        assertEquals("Suite1", result.testSuiteName)
        assertEquals("desc1", result.testSuiteDescription)
        assertTrue(result.isActive)
        assertNotNull(result.creationTimestamp)
        verify(exactly = 1) { testSuiteRepository.save(any()) }
        verify(exactly = 1) { testSuiteVersionRepository.save(any()) }
        verify(exactly = 1) { testSuiteRepository.findLatestVersionByTestSuiteId(savedId) }
    }

    @Test
    fun `getTestPlanVersionsBySuiteId returns mapped DTOs`() {
        // Arrange
        val suiteId = 1
        val suiteVersion = TestSuiteVersion(10, "1", LocalDateTime.now(), null, suiteId)
        val versionPlans = listOf(
            TestSuiteVersionPlan(10, 100, 1),
            TestSuiteVersionPlan(10, 200, 2)
        )
        val planVersion1 = TestPlanVersion(
            testPlanVersionId = 100,
            version = "1.0",
            creationTimestamp = LocalDateTime.now(),
            notes = null,
            appPackage = "pkg",
            mainActivity = null,
            testPlanTestPlanId = 1,
            deviceDeviceId = 1,
            appVersionAppVersionId = 1,
            executionTypeExecutionTypeId = 1
        )
        val planVersion2 = TestPlanVersion(
            testPlanVersionId = 200,
            version = "2.0",
            creationTimestamp = LocalDateTime.now(),
            notes = "notes",
            appPackage = "pkg2",
            mainActivity = "main",
            testPlanTestPlanId = 2,
            deviceDeviceId = 2,
            appVersionAppVersionId = 2,
            executionTypeExecutionTypeId = 2
        )
        val dto1 = mockk<TestPlanVersionResponseDTO>()
        val dto2 = mockk<TestPlanVersionResponseDTO>()

        every { testThresholdRepository.findByTestPlanVersionId(100) } returns emptyList()
        every { testThresholdRepository.findByTestPlanVersionId(200) } returns emptyList()
        every { testMetricParameterRepository.findByTestPlanVersionId(100) } returns emptyList()
        every { testMetricParameterRepository.findByTestPlanVersionId(200) } returns emptyList()
        every { testExecutionTypeParameterRepository.findByTestPlanVersionId(100) } returns emptyList()
        every { testExecutionTypeParameterRepository.findByTestPlanVersionId(200) } returns emptyList()
        every { testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId) } returns suiteVersion
        every { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) } returns versionPlans
        every { testPlanVersionRepository.findById(100) } returns planVersion1
        every { testPlanVersionRepository.findById(200) } returns planVersion2
        every { testPlanVersionMapper.toDto(planVersion1, any(), any(), any(), 10) } returns dto1
        every { testPlanVersionMapper.toDto(planVersion2, any(), any(), any(), 10) } returns dto2

        // Act
        val result = testSuiteService.getTestPlanVersionsBySuiteId(suiteId)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.contains(dto1))
        assertTrue(result.contains(dto2))
        verify(exactly = 1) { testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId) }
        verify(exactly = 1) { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) }
        verify(exactly = 1) { testPlanVersionRepository.findById(100) }
        verify(exactly = 1) { testPlanVersionRepository.findById(200) }
        verify(exactly = 1) { testPlanVersionMapper.toDto(planVersion1, any(), any(), any(), 10) }
        verify(exactly = 1) { testPlanVersionMapper.toDto(planVersion2, any(), any(), any(), 10) }
    }

    @Test
    fun `getTestPlanVersionsBySuiteId throws if no version`() {
        // Arrange
        every { testSuiteVersionRepository.findLatestVersionByTestSuiteId(1) } returns null

        // Act & Assert
        val ex = assertThrows(IllegalArgumentException::class.java) {
            testSuiteService.getTestPlanVersionsBySuiteId(1)
        }
        assertTrue(ex.message!!.contains("has no versions"))
        verify(exactly = 1) { testSuiteVersionRepository.findLatestVersionByTestSuiteId(1) }
    }

    @Test
    fun `runTestSuiteExecution returns SuiteExecutionResponseDTO`() {
        // Arrange
        val suiteId = 1
        val suiteVersion = TestSuiteVersion(10, "1", LocalDateTime.now(), null, suiteId)
        val versionPlans = listOf(
            TestSuiteVersionPlan(10, 100, 1),
            TestSuiteVersionPlan(10, 200, 2)
        )
        val executionResult1 = mockk<TestExecutionResponseDTO>()
        val executionResult2 = mockk<TestExecutionResponseDTO>()

        every { testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId) } returns suiteVersion
        every { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) } returns versionPlans
        every { testExecutionService.runTestExecution(100) } returns executionResult1
        every { testExecutionService.runTestExecution(200) } returns executionResult2
        every { testSuiteExecutionRepository.save(any()) } returns 123

        // Act
        val result = testSuiteService.runTestSuiteExecution(suiteId)

        // Assert
        assertEquals(123, result.suiteExecutionId)
        assertEquals(2, result.executionResults.size)
        assertTrue(result.executionResults.contains(executionResult1))
        assertTrue(result.executionResults.contains(executionResult2))
        assertEquals(suiteVersion.testSuiteVersionId, result.testSuiteVersionTestSuiteVersionId)
        assertNotNull(result.initialTimestamp)
        assertNotNull(result.endTimestamp)
        verify(exactly = 1) { testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId) }
        verify(exactly = 1) { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) }
        verify(exactly = 1) { testExecutionService.runTestExecution(100) }
        verify(exactly = 1) { testExecutionService.runTestExecution(200) }
        verify(exactly = 1) { testSuiteExecutionRepository.save(any()) }
    }

    @Test
    fun `runTestSuiteExecution throws if no version`() {
        // Arrange
        every { testSuiteVersionRepository.findLatestVersionByTestSuiteId(1) } returns null

        // Act & Assert
        val ex = assertThrows(IllegalArgumentException::class.java) {
            testSuiteService.runTestSuiteExecution(1)
        }
        assertTrue(ex.message!!.contains("has no versions"))
        verify(exactly = 1) { testSuiteVersionRepository.findLatestVersionByTestSuiteId(1) }
    }

    @Test
    fun `runTestSuiteExecution throws if no plans`() {
        // Arrange
        val suiteVersion = TestSuiteVersion(10, "1", LocalDateTime.now(), null, 1)
        every { testSuiteVersionRepository.findLatestVersionByTestSuiteId(1) } returns suiteVersion
        every { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) } returns emptyList()

        // Act & Assert
        val ex = assertThrows(IllegalStateException::class.java) {
            testSuiteService.runTestSuiteExecution(1)
        }
        assertTrue(ex.message!!.contains("has no TestPlans"))
        verify(exactly = 1) { testSuiteVersionRepository.findLatestVersionByTestSuiteId(1) }
        verify(exactly = 1) { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) }
    }
}