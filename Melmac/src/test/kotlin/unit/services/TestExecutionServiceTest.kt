import core.runners.ITestRunner
import domain.*
import dtos.*
import io.mockk.*
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*
import services.TestExecutionService

class TestExecutionServiceTest {

    private val testExecutionRepository = mockk<ITestExecutionRepository>()
    private val testPlanVersionRepository = mockk<ITestPlanVersionRepository>()
    private val metricRepository = mockk<IMetricRepository>()
    private val metricOutputRepository = mockk<IMetricOutputRepository>()
    private val executionTypeRepository = mockk<IExecutionTypeRepository>()
    private val deviceRepository = mockk<IDeviceRepository>()
    private val osVersionRepository = mockk<IOperSysVersionRepository>()
    private val osRepository = mockk<IOperSysRepository>()
    private val appVersionRepository = mockk<IAppVersionRepository>()
    private val appRepository = mockk<IAppRepository>()
    private val testPlanExecutionTypeParamValueRepo = mockk<ITestPlanExecutionTypeParameterValueRepository>()
    private val testPlanMetricParamValueRepo = mockk<ITestPlanMetricParameterValueRepository>()
    private val testPlanRepository = mockk<ITestPlanRepository>()
    private val metricParameterRepository = mockk<IMetricParameterRepository>()
    private val executionTypeParameterRepository = mockk<IExecutionTypeParameterRepository>()
    private val testThresholdRepository = mockk<IThresholdRepository>()
    private val thresholdTypeRepository = mockk<IThresholdTypeRepository>()
    private val testMetricOutputResultRepository = mockk<ITestMetricOutputResultRepository>()
    private val testRunner = mockk<ITestRunner>()
    private val testExecutionMapper = mockk<TestExecutionMapper>()
    private val testMetricOutputResultMapper = mockk<TestMetricOutputResultMapper>()

    private val service = TestExecutionService(
        testExecutionRepository,
        testPlanVersionRepository,
        metricRepository,
        metricOutputRepository,
        executionTypeRepository,
        deviceRepository,
        osVersionRepository,
        osRepository,
        appVersionRepository,
        appRepository,
        testPlanExecutionTypeParamValueRepo,
        testPlanMetricParamValueRepo,
        testPlanRepository,
        metricParameterRepository,
        executionTypeParameterRepository,
        testThresholdRepository,
        thresholdTypeRepository,
        testMetricOutputResultRepository,
        testRunner,
        testExecutionMapper,
        testMetricOutputResultMapper
    )

    @Test
    fun `getAllTestExecutions returns mapped DTOs`() {
        // Arrange
        val entity = TestExecution(
            testExecutionId = 1,
            testPlanVersionTestPlanVersionId = 2,
            initialTimestamp = java.time.LocalDateTime.now(),
            endTimestamp = java.time.LocalDateTime.now(),
            passed = "true"
        )
        val dto = TestExecutionResponseDTO(
            testExecutionId = 1,
            initialTimestamp = entity.initialTimestamp,
            endTimestamp = entity.endTimestamp,
            passed = "true",
            testPlanVersionTestPlanVersionId = 2
        )
        every { testExecutionRepository.findAll() } returns listOf(entity)
        every { testExecutionMapper.toDto(entity) } returns dto

        // Act
        val result = service.getAllTestExecutions()

        // Assert
        assertEquals(listOf(dto), result)
        verify { testExecutionRepository.findAll() }
        verify { testExecutionMapper.toDto(entity) }
    }

    @Test
    fun `getTestExecutionById returns mapped DTO when found`() {
        // Arrange
        val entity = TestExecution(
            testExecutionId = 1,
            testPlanVersionTestPlanVersionId = 2,
            initialTimestamp = java.time.LocalDateTime.now(),
            endTimestamp = java.time.LocalDateTime.now(),
            passed = "true"
        )
        val dto = TestExecutionResponseDTO(
            testExecutionId = 1,
            initialTimestamp = entity.initialTimestamp,
            endTimestamp = entity.endTimestamp,
            passed = "true",
            testPlanVersionTestPlanVersionId = 2
        )
        every { testExecutionRepository.findById(1) } returns entity
        every { testExecutionMapper.toDto(entity) } returns dto

        // Act
        val result = service.getTestExecutionById(1)

        // Assert
        assertEquals(dto, result)
        verify { testExecutionRepository.findById(1) }
        verify { testExecutionMapper.toDto(entity) }
    }

    @Test
    fun `getTestExecutionById returns null when not found`() {
        // Arrange
        every { testExecutionRepository.findById(99) } returns null

        // Act
        val result = service.getTestExecutionById(99)

        // Assert
        assertNull(result)
        verify { testExecutionRepository.findById(99) }
    }

    @Test
    fun `runTestExecution runs and saves execution and results`() {
        // Arrange
        val now = java.time.LocalDateTime.now()
        val testPlanVersion = TestPlanVersion(
            testPlanVersionId = 10,
            version = "1",
            creationTimestamp = now,
            notes = "",
            testPlanTestPlanId = 20,
            deviceDeviceId = 30,
            appVersionAppVersionId = 40,
            executionTypeExecutionTypeId = 50,
            appPackage = "pkg",
            mainActivity = "main"
        )
        val testPlan = TestPlan(testPlanId = 20, testName = "plan", metricMetricId = 60)
        val metric = Metric(metricId = 60, metricName = "metric")
        val executionType = ExecutionType(executionTypeId = 50, executionTypeName = "type", executionTypeDescription = null)
        val device = Device(deviceId = 30, deviceName = "dev", deviceSerialNumber = "sn", osVersionOsVersionId = 70)
        val os = OperativeSystem(operSysId = 70, operSysName = "Android")
        val appVersion = AppVersion(appVersionId = 40, appId = 80, appVersion = "1.0")
        val app = App(appId = 80, appName = "app")
        val metricOutput = MetricOutput(metricOutputId = 100, metricMetricId = 60, outputName = "out", unit = "ms")
        val execTypeParamValue = TestExecutionTypeParameter(testExecutionTypeParameterId = 1, parameterValue = "v", executionTypeParameterExecutionTypeParameterId = 2, testPlanVersionTestPlanVersionId = 10)
        val metricParamValue = TestMetricParameter(testMetricParameterId = 3, parameterValue = "v", metricParameterMetricParameterId = 4, testPlanVersionTestPlanVersionId = 10)
        val execTypeParam = ExecutionTypeParameter(2, "etp", "", 50)
        val metricParam = MetricParameter(4, "mp", "", 60)
        val threshold = TestThreshold(1, 100, 5, 10, 100)
        val thresholdType = ThresholdType(5, "MAX", "desc")
        val testExecutionId = 123
        val responseDto = TestExecutionResponseDTO(
            testExecutionId = testExecutionId,
            initialTimestamp = now,
            endTimestamp = now,
            passed = "true",
            testPlanVersionTestPlanVersionId = 10
        )

        every { testPlanVersionRepository.findById(10) } returns testPlanVersion
        every { testPlanRepository.findById(20) } returns testPlan
        every { metricRepository.findById(60) } returns metric
        every { executionTypeRepository.findById(50) } returns executionType
        every { deviceRepository.findById(30) } returns device
        every { osRepository.findById(70) } returns os
        every { appVersionRepository.findById(40) } returns appVersion
        every { appRepository.findById(80) } returns app
        every { testPlanExecutionTypeParamValueRepo.findByTestPlanVersionId(10) } returns listOf(execTypeParamValue)
        every { testPlanMetricParamValueRepo.findByTestPlanVersionId(10) } returns listOf(metricParamValue)
        every { executionTypeParameterRepository.findById(2) } returns execTypeParam
        every { metricParameterRepository.findById(4) } returns metricParam
        every { metricOutputRepository.findByMetricId(60) } returns listOf(metricOutput)
        every { testThresholdRepository.findByTestPlanVersionId(10) } returns listOf(threshold)
        every { thresholdTypeRepository.findById(5) } returns thresholdType
        every { testExecutionMapper.toConfigDto(
            any(), // executionTypeName
            any(), // metricName
            any(), // metricParams
            any(), // executionTypeParams
            any(), // testThresholds
            any(), // deviceName
            any(), // deviceSerialNumber
            any(), // platform
            any(), // appName
            any(), // appVersion
            any(), // appPackage
            any()  // mainActivity
        ) } returns TestExecutionConfigDTO(
            executionTypeName = "type",
            metricName = "metric",
            metricParams = mapOf("mp" to "v"),
            executionTypeParams = mapOf("etp" to "v"),
            testThresholds = listOf(Triple("100", "MAX", "out")),
            deviceName = "dev",
            deviceSerialNumber = "sn",
            platform = "Android",
            appName = "app",
            appVersion = "1.0",
            appPackage = "pkg",
            mainActivity = "main"
        )
        every { testRunner.run(any()) } returns mapOf("success" to "true", "out" to "42")
        every { testExecutionRepository.save(any()) } returns testExecutionId
        every { testMetricOutputResultRepository.save(any()) } returns 1
        every { testExecutionMapper.toDto(any()) } returns responseDto

        // Act
        val result = service.runTestExecution(10)

        // Assert
        assertEquals(responseDto, result)
        verify { testPlanVersionRepository.findById(10) }
        verify { testPlanRepository.findById(20) }
        verify { metricRepository.findById(60) }
        verify { executionTypeRepository.findById(50) }
        verify { deviceRepository.findById(30) }
        verify { osRepository.findById(70) }
        verify { appVersionRepository.findById(40) }
        verify { appRepository.findById(80) }
        verify { testPlanExecutionTypeParamValueRepo.findByTestPlanVersionId(10) }
        verify { testPlanMetricParamValueRepo.findByTestPlanVersionId(10) }
        verify { executionTypeParameterRepository.findById(2) }
        verify { metricParameterRepository.findById(4) }
        verify { metricOutputRepository.findByMetricId(60) }
        verify { testThresholdRepository.findByTestPlanVersionId(10) }
        verify { thresholdTypeRepository.findById(5) }
        verify { testExecutionMapper.toConfigDto(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        verify { testRunner.run(any()) }
        verify { testExecutionRepository.save(any()) }
        verify { testMetricOutputResultRepository.save(any()) }
        verify { testExecutionMapper.toDto(any()) }
    }
}