package unit.TestPlan

import domain.*
import dtos.*
import io.mockk.*
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*
import services.*
import services.IServices.IAppService
import services.IServices.IDeviceService

class TestPlanServiceTest {

    // --- Mocks ---
    private val testPlanRepository = mockk<ITestPlanRepository>()
    private val testPlanVersionRepository = mockk<ITestPlanVersionRepository>()
    private val testSuiteRepository = mockk<ITestSuiteRepository>()
    private val testThresholdRepository = mockk<IThresholdRepository>()
    private val testPlanMetricParameterRepository = mockk<ITestPlanMetricParameterValueRepository>()
    private val testPlanExecutionTypeParameterRepository =
            mockk<ITestPlanExecutionTypeParameterValueRepository>()
    private val testSuiteVersionPlanRepository = mockk<ITestSuiteVersionPlanRepository>()
    private val deviceRepository = mockk<IDeviceRepository>()
    private val appRepository = mockk<IAppRepository>()
    private val appVersionRepository = mockk<IAppVersionRepository>()
    private val osRepository = mockk<IOperSysRepository>()
    private val osVersionRepository = mockk<IOperSysVersionRepository>()
    private val deviceService = mockk<IDeviceService>()
    private val appService = mockk<IAppService>()
    private val metricRepository = mockk<IMetricRepository>()
    private val thresholdTypeRepository = mockk<IThresholdTypeRepository>()
    private val executionTypeRepository = mockk<IExecutionTypeRepository>()
    private val metricParameterRepository = mockk<IMetricParameterRepository>()
    private val executionTypeParameterRepository = mockk<IExecutionTypeParameterRepository>()
    private val testThresholdMapper = mockk<TestThresholdMapper>()
    private val testMetricParameterMapper = mockk<TestMetricParameterMapper>()
    private val testExecutionTypeParameterMapper = mockk<TestExecutionTypeParameterMapper>()
    private val testPlanVersionMapper = mockk<TestPlanVersionMapper>()

    private val service =
            TestPlanService(
                    testPlanRepository,
                    testPlanVersionRepository,
                    testSuiteRepository,
                    testThresholdRepository,
                    testPlanMetricParameterRepository,
                    testPlanExecutionTypeParameterRepository,
                    testSuiteVersionPlanRepository,
                    deviceRepository,
                    appRepository,
                    appVersionRepository,
                    osRepository,
                    osVersionRepository,
                    deviceService,
                    appService,
                    metricRepository,
                    thresholdTypeRepository,
                    executionTypeRepository,
                    metricParameterRepository,
                    executionTypeParameterRepository,
                    testThresholdMapper,
                    testMetricParameterMapper,
                    testExecutionTypeParameterMapper,
                    testPlanVersionMapper
            )

    companion object {
        val metric = Metric(metricId = 2, metricName = "metricA")
        val testPlanId = 1
        val deviceDto =
                AvailableDeviceDTO(
                        id = null,
                        deviceName = "deviceA",
                        deviceSerialNumber = "serialA",
                        osName = "Android",
                        osVersion = "10"
                )
        val os = OperativeSystem(operSysId = 3, operSysName = "Android")
        val osVersion = OSVersion(osVersionId = 4, version = "10", operativeSystemOperSysId = 3)
        val device =
                Device(
                        deviceId = 5,
                        deviceName = "deviceA",
                        deviceSerialNumber = "serialA",
                        osVersionOsVersionId = 4
                )
        val app = App(appId = 6, appName = "appA")
        val appVersion = AppVersion(appVersionId = 7, appId = 6, appVersion = "1.0")
        val executionType =
                ExecutionType(
                        executionTypeId = 8,
                        executionTypeName = "typeA",
                        executionTypeDescription = null
                )
        val thresholdType =
                ThresholdType(
                        thresholdTypeId = 11,
                        thresholdTypeName = "MAX",
                        thresholdTypeDescription = "Maximum"
                )
        val testThreshold =
                TestThreshold(
                        testThresholdId = 12,
                        targetValue = 100,
                        thresholdTypeThresholdTypeId = 11,
                        testPlanVersionTestPlanVersionId = 9,
                        metricOutputMetricOutputId = 1
                )
        val metricParameter =
                TestMetricParameter(
                        testMetricParameterId = 13,
                        parameterValue = "valA",
                        metricParameterMetricParameterId = 14,
                        testPlanVersionTestPlanVersionId = 9
                )
        val executionTypeParameter =
                TestExecutionTypeParameter(
                        testExecutionTypeParameterId = 15,
                        parameterValue = "valB",
                        executionTypeParameterExecutionTypeParameterId = 16,
                        testPlanVersionTestPlanVersionId = 9
                )
        val testPlanVersionResponseDTO = mockk<TestPlanVersionResponseDTO>()
    }

    private fun setupCommonMocks() {
        every { metricRepository.findByName("metricA") } returns metric
        every { testPlanRepository.save(any()) } returns testPlanId
        every { deviceService.getDeviceByName("deviceA") } returns deviceDto
        every { osRepository.findByName("Android") } returns os
        every { osVersionRepository.findByOperSysId(3) } returns listOf(osVersion)
        every { deviceRepository.findByName("deviceA") } returns device
        every { appRepository.findByName("appA") } returns app
        every { appVersionRepository.findByAppIdAndVersion(6, "1.0") } returns appVersion
        every { executionTypeRepository.findByName("typeA") } returns executionType
        every { testPlanVersionRepository.findLatestVersionByTestPlanId(testPlanId) } returns null
        every { testPlanVersionRepository.save(any()) } returns 9
        every { thresholdTypeRepository.findByName("MAX") } returns thresholdType
        every { testThresholdMapper.fromRequestDto(any(), 9, 11) } returns testThreshold
        every { testThresholdRepository.save(testThreshold) } returns 12
        every { testThresholdMapper.toDto(testThreshold.copy(testThresholdId = 12)) } returns
                mockk()
        every { metricParameterRepository.findByMetricIdAndName(2, "valA") } returns
                MetricParameter(14, "paramA", "", 2)
        every { testMetricParameterMapper.fromRequestDto(any(), 9, 14) } returns metricParameter
        every { testPlanMetricParameterRepository.save(metricParameter) } returns 13
        every {
            testMetricParameterMapper.toDto(metricParameter.copy(testMetricParameterId = 13))
        } returns mockk()
        every { executionTypeParameterRepository.findByExecutionTypeIdAndName(8, "valB") } returns
                ExecutionTypeParameter(16, "paramB", "", 8)
        every { testExecutionTypeParameterMapper.fromRequestDto(any(), 9, 16) } returns
                executionTypeParameter
        every { testPlanExecutionTypeParameterRepository.save(executionTypeParameter) } returns 15
        every {
            testExecutionTypeParameterMapper.toDto(
                    executionTypeParameter.copy(testExecutionTypeParameterId = 15)
            )
        } returns mockk()
        every { testSuiteVersionPlanRepository.findByTestSuiteVersionId(10) } returns emptyList()
        every { testSuiteVersionPlanRepository.save(any()) } returns 1
        every { testPlanVersionMapper.toDto(any(), any(), any(), any(), 10) } returns
                testPlanVersionResponseDTO
    }

    @Test
    fun `getTestPlanById returns DTO when found`() {
        // Arrange
        val testPlan = TestPlan(testPlanId = 1, testName = "PlanA", metricMetricId = 2)
        every { testPlanRepository.findById(1) } returns testPlan

        // Act
        val result = service.getTestPlanById(1)

        // Assert
        assertNotNull(result)
        assertEquals(1, result!!.testPlanId)
        assertEquals("PlanA", result.testName)
        assertEquals(2, result.metricMetricId)
        verify(exactly = 1) { testPlanRepository.findById(1) }
    }

    @Test
    fun `getTestPlanById returns null when not found`() {
        // Arrange
        every { testPlanRepository.findById(99) } returns null

        // Act
        val result = service.getTestPlanById(99)

        // Assert
        assertNull(result)
        verify(exactly = 1) { testPlanRepository.findById(99) }
    }

    @Test
    fun `createTestPlanWithVersion creates and returns DTO`() {
        // Arrange
        setupCommonMocks()
        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = listOf(TestThresholdRequestDTO(100, "MAX", 1, 1)),
                        metricParameters =
                                listOf(TestMetricParameterRequestDTO("paramA", "valA", 9)),
                        executionTypeParameters =
                                listOf(TestExecutionTypeParameterRequestDTO("paramB", "valB", 9)),
                        testSuiteVersionId = 10
                )

        // Act
        val result = service.createTestPlanWithVersion(request)

        // Assert
        assertEquals(testPlanVersionResponseDTO, result)
        verify { metricRepository.findByName("metricA") }
        verify { testPlanRepository.save(any()) }
        verify { deviceService.getDeviceByName("deviceA") }
        verify { osRepository.findByName("Android") }
        verify { osVersionRepository.findByOperSysId(3) }
        verify { deviceRepository.findByName("deviceA") }
        verify { appRepository.findByName("appA") }
        verify { appVersionRepository.findByAppIdAndVersion(6, "1.0") }
        verify { executionTypeRepository.findByName("typeA") }
        verify { testPlanVersionRepository.save(any()) }
        verify { thresholdTypeRepository.findByName("MAX") }
        verify { testThresholdRepository.save(any()) }
        verify { metricParameterRepository.findByMetricIdAndName(2, "valA") }
        verify { testPlanMetricParameterRepository.save(any()) }
        verify { executionTypeParameterRepository.findByExecutionTypeIdAndName(8, "valB") }
        verify { testPlanExecutionTypeParameterRepository.save(any()) }
        verify { testSuiteVersionPlanRepository.save(any()) }
        verify { testPlanVersionMapper.toDto(any(), any(), any(), any(), 10) }
    }

    @Test
    fun `createTestPlanWithVersion throws when metric not found`() {
        // Arrange
        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )
        every { metricRepository.findByName("metricA") } returns null

        // Act & Assert
        val ex =
                assertThrows(IllegalArgumentException::class.java) {
                    service.createTestPlanWithVersion(request)
                }
        assertTrue(ex.message!!.contains("Metric not found"))
        verify { metricRepository.findByName("metricA") }
    }

    @Test
    fun `createTestPlanWithVersion creates new OS if not found`() {
        setupCommonMocks()
        every { osRepository.findByName("Android") } returns null
        every { osRepository.save(any()) } returns 99
        every { osVersionRepository.findByOperSysId(99) } returns listOf(osVersion)

        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )

        service.createTestPlanWithVersion(request)

        verify { osRepository.save(match { it.operSysName == "Android" }) }
        verify { osVersionRepository.findByOperSysId(99) }
    }

    @Test
    fun `createTestPlanWithVersion creates new OSVersion if not found`() {
        setupCommonMocks()
        every { osVersionRepository.findByOperSysId(3) } returns emptyList()
        every { osVersionRepository.save(any()) } returns 123

        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )

        service.createTestPlanWithVersion(request)

        verify {
            osVersionRepository.save(
                    match { it.version == "10" && it.operativeSystemOperSysId == 3 }
            )
        }
    }
    @Test
    fun `createTestPlanWithVersion creates new Device if not found`() {
        setupCommonMocks()
        every { deviceRepository.findByName("deviceA") } returns null
        every { deviceRepository.save(any()) } returns 55

        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )

        service.createTestPlanWithVersion(request)

        verify { deviceRepository.save(match { it.deviceName == "deviceA" }) }
    }

    @Test
    fun `createTestPlanWithVersion creates new App if not found`() {
        setupCommonMocks()
        every { appRepository.findByName("appA") } returns null
        val appDto = AppResponseDTO(appId = 66, appName = "appA")
        every { appService.getAppByNameFromFolder("appA") } returns appDto
        every { appRepository.save(any()) } returns 66
        every { appVersionRepository.findByAppIdAndVersion(66, "1.0") } returns appVersion
    
        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )
    
        service.createTestPlanWithVersion(request)
    
        verify { appService.getAppByNameFromFolder("appA") }
        verify { appRepository.save(match { it.appName == "appA" }) }
        verify { appVersionRepository.findByAppIdAndVersion(66, "1.0") }
    }

    @Test
    fun `createTestPlanWithVersion creates new AppVersion if not found`() {
        setupCommonMocks()
        every { appVersionRepository.findByAppIdAndVersion(6, "1.0") } returns null
        val appVersionDto = AppVersionResponseDTO(appVersionId = 1, appId = 1, appVersion = "1.0")
        every { appService.getAppVersionByNameFromFolder("appA", "1.0") } returns appVersionDto
        every { appVersionRepository.save(any()) } returns 77

        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "1.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )

        service.createTestPlanWithVersion(request)

        verify { appService.getAppVersionByNameFromFolder("appA", "1.0") }
        verify { appVersionRepository.save(match { it.appVersion == "1.0" }) }
    }

    @Test
    fun `createTestPlanWithVersion throws if AppVersion not found in folder`() {
        setupCommonMocks()
        every { appVersionRepository.findByAppIdAndVersion(6, "2.0") } returns null
        val appVersionDto = AppVersionResponseDTO(appVersionId = 1, appId = 1, appVersion = "")
        every { appService.getAppVersionByNameFromFolder("appA", "2.0") } returns appVersionDto
    
        val request =
                TestPlanVersionRequestDTO(
                        testName = "PlanA",
                        metricName = "metricA",
                        deviceName = "deviceA",
                        appName = "appA",
                        appVersion = "2.0",
                        executionType = "typeA",
                        notes = "notes",
                        appPackage = "pkg",
                        mainActivity = "main",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 10
                )
    
        val ex =
                assertThrows(IllegalArgumentException::class.java) {
                    service.createTestPlanWithVersion(request)
                }
        assertTrue(ex.message!!.contains("App version not found in folder"))
        verify { appService.getAppVersionByNameFromFolder("appA", "2.0") }
    }
}
