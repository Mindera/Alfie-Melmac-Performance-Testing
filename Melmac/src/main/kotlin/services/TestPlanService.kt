package services

import domain.*
import dtos.*
import java.time.LocalDateTime
import mappers.TestExecutionTypeParameterMapper
import mappers.TestMetricParameterMapper
import mappers.TestPlanVersionMapper
import mappers.TestThresholdMapper
import repos.IRepos.*
import services.IServices.IAppService
import services.IServices.IDeviceService
import services.IServices.ITestPlanService

/**
 * Service implementation for managing test plans and their versions. Handles creation, retrieval,
 * and association of test plans, devices, apps, parameters, and thresholds.
 *
 * @property testPlanRepository Repository for TestPlan entities.
 * @property testPlanVersionRepository Repository for TestPlanVersion entities.
 * @property testSuiteRepository Repository for TestSuite entities.
 * @property testThresholdRepository Repository for Threshold entities.
 * @property testPlanMetricParameterRepository Repository for TestPlanMetricParameterValue entities.
 * @property testPlanExecutionTypeParameterRepository Repository for
 * TestPlanExecutionTypeParameterValue entities.
 * @property testSuiteVersionPlanRepository Repository for TestSuiteVersionPlan entities.
 * @property deviceRepository Repository for Device entities.
 * @property appRepository Repository for App entities.
 * @property appVersionRepository Repository for AppVersion entities.
 * @property osRepository Repository for OperativeSystem entities.
 * @property osVersionRepository Repository for OSVersion entities.
 * @property deviceService Service for device-related operations.
 * @property appService Service for app-related operations.
 * @property metricRepository Repository for Metric entities.
 * @property thresholdTypeRepository Repository for ThresholdType entities.
 * @property executionTypeRepository Repository for ExecutionType entities.
 */
class TestPlanService(
        private val testPlanRepository: ITestPlanRepository,
        private val testPlanVersionRepository: ITestPlanVersionRepository,
        private val testSuiteRepository: ITestSuiteRepository,
        private val testThresholdRepository: IThresholdRepository,
        private val testPlanMetricParameterRepository: ITestPlanMetricParameterValueRepository,
        private val testPlanExecutionTypeParameterRepository:
                ITestPlanExecutionTypeParameterValueRepository,
        private val testSuiteVersionPlanRepository: ITestSuiteVersionPlanRepository,
        private val deviceRepository: IDeviceRepository,
        private val appRepository: IAppRepository,
        private val appVersionRepository: IAppVersionRepository,
        private val osRepository: IOperSysRepository,
        private val osVersionRepository: IOperSysVersionRepository,
        private val deviceService: IDeviceService,
        private val appService: IAppService,
        private val metricRepository: IMetricRepository,
        private val thresholdTypeRepository: IThresholdTypeRepository,
        private val executionTypeRepository: IExecutionTypeRepository,
        private val metricParameterRepository: IMetricParameterRepository,
        private val executionTypeParameterRepository: IExecutionTypeParameterRepository,
        private val testThresholdMapper: TestThresholdMapper,
        private val testMetricParameterMapper: TestMetricParameterMapper,
        private val testExecutionTypeParameterMapper: TestExecutionTypeParameterMapper,
        private val testPlanVersionMapper: TestPlanVersionMapper
) : ITestPlanService {

        /**
         * Retrieves a test plan by its ID.
         *
         * @param id The ID of the test plan.
         * @return [TestPlanResponseDTO] for the test plan, or null if not found.
         */
        override fun getTestPlanById(id: Int): TestPlanResponseDTO? {
                val testPlan = testPlanRepository.findById(id)
                if (testPlan == null || testPlan.testPlanId == null) {
                        return null
                }
                return TestPlanResponseDTO(
                        testPlanId = testPlan.testPlanId,
                        testName = testPlan.testName,
                        metricMetricId = testPlan.metricMetricId
                )
        }

        /**
         * Creates a new test plan and its version, including all related entities such as device,
         * app, app version, thresholds, metric parameters, execution type parameters, and suite
         * associations.
         *
         * @param request The [TestPlanVersionRequestDTO] containing all required information.
         * @return [TestPlanVersionResponseDTO] representing the created test plan version and its
         * associations.
         * @throws IllegalArgumentException if required entities are not found or invalid.
         * @throws IllegalStateException if required IDs are missing after creation.
         */
        override fun createTestPlanWithVersion(
                request: TestPlanVersionRequestDTO
        ): TestPlanVersionResponseDTO {
                println("Starting createTestPlanWithVersion")
                val metric = getMetric(request.metricName)
                val testPlanId = createTestPlan(request, metric)
                val device = getDevice(request.deviceName)
                val os = getOrCreateOS(device.osName)
                val osVersion = getOrCreateOSVersion(os, device.osVersion)
                val databaseDevice = getOrCreateDevice(device, os, osVersion)
                val app = getOrCreateApp(request.appName)
                val appVersionDef = getOrCreateAppVersion(app, request.appVersion)
                val executionType = getExecutionType(request.executionType)
                val savedTestPlanVersion =
                        createTestPlanVersion(
                                request,
                                testPlanId,
                                databaseDevice,
                                appVersionDef,
                                executionType
                        )
                val savedThresholds = saveThresholds(request, savedTestPlanVersion)
                val savedMetricParameters =
                        saveMetricParameters(request, savedTestPlanVersion, metric)
                val savedExecutionTypeParameters =
                        saveExecutionTypeParameters(request, savedTestPlanVersion)
                saveTestSuiteVersionPlan(request, savedTestPlanVersion)
                return testPlanVersionMapper.toDto(
                        savedTestPlanVersion,
                        savedThresholds.map { testThresholdMapper.toDto(it) },
                        savedMetricParameters.map { testMetricParameterMapper.toDto(it) },
                        savedExecutionTypeParameters.map {
                                testExecutionTypeParameterMapper.toDto(it)
                        },
                        request.testSuiteVersionId
                )
        }

        /**
         * Retrieves a [Metric] entity by its name.
         *
         * @param metricName The name of the metric.
         * @return The [Metric] entity.
         * @throws IllegalArgumentException if the metric is not found.
         */
        private fun getMetric(metricName: String): Metric {
                return metricRepository.findByName(metricName)
                        ?: throw IllegalArgumentException("Metric not found")
        }

        /**
         * Creates a new [TestPlan] entity.
         *
         * @param request The [TestPlanVersionRequestDTO] containing test plan details.
         * @param metric The [Metric] associated with the test plan.
         * @return The ID of the newly created test plan.
         * @throws IllegalStateException if the metric ID is null.
         */
        private fun createTestPlan(request: TestPlanVersionRequestDTO, metric: Metric): Int {
                val newTestPlan =
                        TestPlan(
                                testPlanId = null,
                                testName = request.testName,
                                metricMetricId = metric.metricId
                                                ?: throw IllegalStateException(
                                                        "Metric ID cannot be null"
                                                )
                        )
                println("Saving new TestPlan: $newTestPlan")
                return testPlanRepository.save(newTestPlan)
        }

        /**
         * Retrieves a device by its name.
         *
         * @param deviceName The name of the device.
         * @return [AvailableDeviceDTO] for the device.
         * @throws IllegalArgumentException if the device is not found.
         */
        private fun getDevice(deviceName: String): AvailableDeviceDTO {
                println("Looking up device by name: $deviceName")
                return deviceService.getDeviceByName(deviceName)
                        ?: throw IllegalArgumentException("Device not found")
        }

        /**
         * Retrieves or creates an [OperativeSystem] entity by its name.
         *
         * @param osName The name of the operating system.
         * @return The [OperativeSystem] entity.
         */
        private fun getOrCreateOS(osName: String): OperativeSystem {
                println("Looking up OS by name: $osName")
                return osRepository.findByName(osName)
                        ?: OperativeSystem(
                                operSysId =
                                        osRepository.save(
                                                OperativeSystem(
                                                        operSysId = null,
                                                        operSysName = osName
                                                )
                                        ),
                                operSysName = osName
                        )
        }

        /**
         * Retrieves or creates an [OSVersion] entity for a given OS.
         *
         * @param os The [OperativeSystem] entity.
         * @param osVersionName The version name.
         * @return The [OSVersion] entity.
         * @throws IllegalStateException if the OS ID is null.
         */
        private fun getOrCreateOSVersion(os: OperativeSystem, osVersionName: String): OSVersion {
                val operSysId = os.operSysId ?: throw IllegalStateException("OS ID cannot be null")
                println("Looking up OS versions for OS ID: $operSysId")
                val osVersions = osVersionRepository.findByOperSysId(operSysId)
                return if (osVersions.isNotEmpty()) {
                        println("Using existing OS version: ${osVersions.first()}")
                        osVersions.first()
                } else {
                        println("Creating new OS version for device: $osVersionName")
                        val newOsVersion =
                                OSVersion(
                                        osVersionId = null,
                                        version = osVersionName,
                                        operativeSystemOperSysId = operSysId
                                )
                        val savedOsVersionId = osVersionRepository.save(newOsVersion)
                        println("Saved new OS version with ID: $savedOsVersionId")
                        newOsVersion.copy(osVersionId = savedOsVersionId)
                }
        }

        /**
         * Retrieves or creates a [Device] entity.
         *
         * @param device The [AvailableDeviceDTO] containing device details.
         * @param os The [OperativeSystem] entity.
         * @param osVersion The [OSVersion] entity.
         * @return The [Device] entity.
         * @throws IllegalArgumentException if required device details are missing.
         * @throws IllegalStateException if the OS Version ID is null.
         */
        private fun getOrCreateDevice(
                device: AvailableDeviceDTO,
                os: OperativeSystem,
                osVersion: OSVersion
        ): Device {
                return if (os.operSysName.equals("ios", ignoreCase = true)) {
                        deviceRepository.findBySerialNumber(
                                device.deviceSerialNumber
                                        ?: throw IllegalArgumentException(
                                                "Device serial number cannot be null"
                                        )
                        )
                } else {
                        deviceRepository.findByName(device.deviceName)
                }
                        ?: run {
                                println("Device not found in DB, creating new device...")
                                val newDevice =
                                        Device(
                                                deviceId = null,
                                                deviceName = device.deviceName,
                                                deviceSerialNumber = device.deviceSerialNumber,
                                                osVersionOsVersionId = osVersion.osVersionId
                                                                ?: throw IllegalStateException(
                                                                        "OS Version ID cannot be null"
                                                                )
                                        )
                                val savedDeviceId = deviceRepository.save(newDevice)
                                println("Saved new device with ID: $savedDeviceId")
                                newDevice.copy(deviceId = savedDeviceId)
                        }
        }

        /**
         * Retrieves or creates an [App] entity by its name.
         *
         * @param appName The name of the app.
         * @return The [App] entity.
         */
        private fun getOrCreateApp(appName: String): App {
                println("Looking up app by name: $appName")
                return appRepository.findByName(appName)
                        ?: run {
                                println("App not found in DB, looking in folder...")
                                val appInFolderDto = appService.getAppByNameFromFolder(appName)
                                val appInFolder =
                                        App(appId = null, appName = appInFolderDto.appName)
                                val newAppId = appRepository.save(appInFolder)
                                println("Saved new app with ID: $newAppId")
                                appInFolder.copy(appId = newAppId)
                        }
        }

        /**
         * Retrieves or creates an [AppVersion] entity for a given app.
         *
         * @param app The [App] entity.
         * @param appVersion The version string.
         * @return The [AppVersion] entity.
         * @throws IllegalArgumentException if the app version is not found in the folder.
         */
        private fun getOrCreateAppVersion(app: App, appVersion: String): AppVersion {
                println("Looking up app version: $appVersion for appId: ${app.appId}")
                val existingAppVersion =
                        appVersionRepository.findByAppIdAndVersion(app.appId!!, appVersion)
                return if (existingAppVersion != null) {
                        println("Found existing app version: $existingAppVersion")
                        existingAppVersion
                } else {
                        println("App version not found in DB, looking in folder...")
                        val appVersionInFolderDto =
                                appService.getAppVersionByNameFromFolder(app.appName, appVersion)
                        if (appVersionInFolderDto.appVersion.isEmpty()) {
                                throw IllegalArgumentException("App version not found in folder")
                        }
                        val newAppVersion =
                                AppVersion(
                                        appVersionId = null,
                                        appId = app.appId,
                                        appVersion = appVersionInFolderDto.appVersion
                                )
                        val savedAppVersionId = appVersionRepository.save(newAppVersion)
                        println("Saved new app version with ID: $savedAppVersionId")
                        newAppVersion.copy(appVersionId = savedAppVersionId)
                }
        }

        /**
         * Retrieves an [ExecutionType] entity by its name.
         *
         * @param executionTypeName The name of the execution type.
         * @return The [ExecutionType] entity.
         * @throws IllegalArgumentException if the execution type is not found.
         */
        private fun getExecutionType(executionTypeName: String): ExecutionType {
                return executionTypeRepository.findByName(executionTypeName)
                        ?: throw IllegalArgumentException(
                                "Execution type not found: $executionTypeName"
                        )
        }

        /**
         * Creates a new [TestPlanVersion] entity.
         *
         * @param request The [TestPlanVersionRequestDTO] containing version details.
         * @param testPlanId The ID of the test plan.
         * @param databaseDevice The [Device] entity.
         * @param appVersionDef The [AppVersion] entity.
         * @param executionType The [ExecutionType] entity.
         * @return The newly created [TestPlanVersion] entity.
         */
        private fun createTestPlanVersion(
                request: TestPlanVersionRequestDTO,
                testPlanId: Int,
                databaseDevice: Device,
                appVersionDef: AppVersion,
                executionType: ExecutionType
        ): TestPlanVersion {
                val newVersion =
                        TestPlanVersion(
                                testPlanVersionId = null,
                                version =
                                        (testPlanVersionRepository
                                                        .findLatestVersionByTestPlanId(testPlanId)
                                                        ?.version
                                                        ?.plus(1)
                                                        ?: 1).toString(),
                                creationTimestamp = LocalDateTime.now(),
                                notes = request.notes,
                                testPlanTestPlanId = testPlanId,
                                deviceDeviceId = databaseDevice.deviceId!!,
                                appVersionAppVersionId = appVersionDef.appVersionId!!,
                                executionTypeExecutionTypeId = executionType.executionTypeId!!,
                                appPackage = request.appPackage,
                                mainActivity = request.mainActivity
                        )
                println("Saving new TestPlanVersion: $newVersion")
                val savedTestPlanVersionId = testPlanVersionRepository.save(newVersion)
                println("Saved TestPlanVersion")
                return newVersion.copy(testPlanVersionId = savedTestPlanVersionId)
        }

        /**
         * Saves threshold entities for a test plan version.
         *
         * @param request The [TestPlanVersionRequestDTO] containing threshold details.
         * @param savedTestPlanVersion The [TestPlanVersion] entity.
         * @return List of saved [TestThreshold] entities.
         * @throws IllegalArgumentException if a threshold type is not found.
         */
        private fun saveThresholds(
                request: TestPlanVersionRequestDTO,
                savedTestPlanVersion: TestPlanVersion
        ): List<TestThreshold> {
                println("Mapping thresholds...")
                val testThreshold =
                        request.thresholds.map { threshold: TestThresholdRequestDTO ->
                                val thresholdType =
                                        thresholdTypeRepository.findByName(threshold.thresholdType)
                                                ?: throw IllegalArgumentException(
                                                        "Threshold type not found: ${threshold.thresholdType}"
                                                )
                                testThresholdMapper.fromRequestDto(
                                        threshold,
                                        savedTestPlanVersion.testPlanVersionId!!,
                                        thresholdType.thresholdTypeId!!
                                )
                        }
                println("Saving thresholds: $testThreshold")
                return testThreshold.map { threshold ->
                        val id = testThresholdRepository.save(threshold)
                        threshold.copy(testThresholdId = id)
                }
        }

        /**
         * Saves metric parameter entities for a test plan version.
         *
         * @param request The [TestPlanVersionRequestDTO] containing metric parameter details.
         * @param savedTestPlanVersion The [TestPlanVersion] entity.
         * @return List of saved [TestMetricParameter] entities.
         */
        private fun saveMetricParameters(
                request: TestPlanVersionRequestDTO,
                savedTestPlanVersion: TestPlanVersion,
                metric: Metric
        ): List<TestMetricParameter> {
                println("Mapping metric parameters...")
                val testMetricParameters =
                        request.metricParameters.map { parameter: TestMetricParameterRequestDTO ->
                                val metricParameter =
                                        metricParameterRepository.findByMetricIdAndName(
                                                metric.metricId!!,
                                                parameter.metricParameter
                                        )
                                                ?: throw IllegalArgumentException(
                                                        "Metric parameter not found: ${parameter.metricParameter}"
                                                )
                                testMetricParameterMapper.fromRequestDto(
                                        parameter,
                                        savedTestPlanVersion.testPlanVersionId!!,
                                        metricParameter.metricParameterId!!
                                )
                        }
                println("Saving metric parameters: $testMetricParameters")
                return testMetricParameters.map { parameter ->
                        val id = testPlanMetricParameterRepository.save(parameter)
                        parameter.copy(testMetricParameterId = id)
                }
        }

        /**
         * Saves execution type parameter entities for a test plan version.
         *
         * @param request The [TestPlanVersionRequestDTO] containing execution type parameter
         * details.
         * @param savedTestPlanVersion The [TestPlanVersion] entity.
         * @return List of saved [TestExecutionTypeParameter] entities.
         */
        private fun saveExecutionTypeParameters(
                request: TestPlanVersionRequestDTO,
                savedTestPlanVersion: TestPlanVersion
        ): List<TestExecutionTypeParameter> {
                println("Mapping execution type parameters...")
                val testExecutionTypeParameters =
                        request.executionTypeParameters.map {
                                parameter: TestExecutionTypeParameterRequestDTO ->
                                val executionTypeParameter =
                                        executionTypeParameterRepository
                                                .findByExecutionTypeIdAndName(
                                                        savedTestPlanVersion
                                                                .executionTypeExecutionTypeId,
                                                        parameter.executionTypeParameter
                                                )
                                                ?: throw IllegalArgumentException(
                                                        "Execution type parameter not found: ${parameter.executionTypeParameter}"
                                                )
                                testExecutionTypeParameterMapper.fromRequestDto(
                                        parameter,
                                        savedTestPlanVersion.testPlanVersionId!!,
                                        executionTypeParameter.executionTypeParameterId!!
                                )
                        }
                println("Saving execution type parameters: $testExecutionTypeParameters")
                return testExecutionTypeParameters.map { parameter ->
                        val id = testPlanExecutionTypeParameterRepository.save(parameter)
                        parameter.copy(testExecutionTypeParameterId = id)
                }
        }

        /**
         * Saves a [TestSuiteVersionPlan] entity associating a test suite version with a test plan
         * version.
         *
         * @param request The [TestPlanVersionRequestDTO] containing suite version details.
         * @param savedTestPlanVersion The [TestPlanVersion] entity.
         */
        private fun saveTestSuiteVersionPlan(
                request: TestPlanVersionRequestDTO,
                savedTestPlanVersion: TestPlanVersion
        ) {
                println(
                        "Saving TestSuiteVersionPlan for testSuiteVersionId: ${request.testSuiteVersionId}"
                )
                val testSuitePlanVersion =
                        TestSuiteVersionPlan(
                                testSuiteVersionTestSuiteVersionId = request.testSuiteVersionId,
                                testPlanVersionTestPlanVersionId =
                                        savedTestPlanVersion.testPlanVersionId!!,
                                order =
                                        testSuiteVersionPlanRepository.findByTestSuiteVersionId(
                                                        request.testSuiteVersionId
                                                )
                                                .size + 1
                        )
                testSuiteVersionPlanRepository.save(testSuitePlanVersion)
                println("Saved TestSuiteVersionPlan")
        }
}
