package services

import domain.*
import dtos.*
import java.time.LocalDateTime
import repos.IRepos.*
import services.IServices.IAppService
import services.IServices.IDeviceService
import services.IServices.ITestPlanService

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
) : ITestPlanService {

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

        override fun createTestPlanWithVersion(
                request: TestPlanVersionRequestDTO
        ): TestPlanVersionResponseDTO {
                println("Starting createTestPlanWithVersion")
                val newTestPlan =
                        TestPlan(
                                testPlanId = null,
                                testName = request.testName,
                                metricMetricId = request.metricMetricId
                        )
                println("Saving new TestPlan: $newTestPlan")
                val testPlanId = testPlanRepository.save(newTestPlan)
                println("Saved TestPlan with ID: $testPlanId")

                val deviceName = request.deviceName
                println("Looking up device by name: $deviceName")
                val device =
                        deviceService.getDeviceByName(deviceName)
                                ?: throw IllegalArgumentException("Device not found")
                println("Found device: $device")

                println("Looking up OS by name: ${device.osName}")
                val os =
                        osRepository.findByName(device.osName)
                                ?: OperativeSystem(
                                        operSysId =
                                                osRepository.save(
                                                        OperativeSystem(
                                                                operSysId = null,
                                                                operSysName = device.osName
                                                        )
                                                ),
                                        operSysName = device.osName
                                )
                println("Using OS: $os")

                val operSysId = os.operSysId ?: throw IllegalStateException("OS ID cannot be null")
                println("Looking up OS versions for OS ID: $operSysId")
                val osVersions = osVersionRepository.findByOperSysId(operSysId)
                println("Found OS versions: $osVersions")

                val osVersion =
                        if (osVersions.isNotEmpty()) {
                                println("Using existing OS version: ${osVersions.first()}")
                                osVersions.first()
                        } else {
                                println("Creating new OS version for device: ${device.osVersion}")
                                val newOsVersion =
                                        OSVersion(
                                                osVersionId = null,
                                                version = device.osVersion,
                                                operativeSystemOperSysId = operSysId
                                        )
                                val savedOsVersionId = osVersionRepository.save(newOsVersion)
                                println("Saved new OS version with ID: $savedOsVersionId")
                                newOsVersion.copy(osVersionId = savedOsVersionId)
                        }

                val databaseDevice =
                        if (os.operSysName.equals("ios", ignoreCase = true)) {
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
                                                        deviceSerialNumber =
                                                                device.deviceSerialNumber,
                                                        osVersionOsVersionId = osVersion.osVersionId
                                                                        ?: throw IllegalStateException(
                                                                                "OS Version ID cannot be null"
                                                                        )
                                                )
                                        val savedDeviceId = deviceRepository.save(newDevice)
                                        println("Saved new device with ID: $savedDeviceId")
                                        newDevice.copy(deviceId = savedDeviceId)
                                }

                val appName = request.appName
                println("Looking up app by name: $appName")
                val app =
                        appRepository.findByName(appName)
                                ?: run {
                                        println("App not found in DB, looking in folder...")
                                        val appInFolderDto =
                                                appService.getAppByNameFromFolder(appName)
                                        val appInFolder =
                                                App(
                                                        appId = null,
                                                        appName = appInFolderDto.appName,
                                                )
                                        val newAppId = appRepository.save(appInFolder)
                                        println("Saved new app with ID: $newAppId")
                                        appInFolder.copy(appId = newAppId)
                                }
                println("Using app: $app")

                val appVersion = request.appVersion
                println("Looking up app version: $appVersion for appId: ${app.appId}")
                val existingAppVersion =
                        appVersionRepository.findByAppIdAndVersion(app.appId!!, appVersion)
                val appVersionDef =
                        if (existingAppVersion != null) {
                                println("Found existing app version: $existingAppVersion")
                                existingAppVersion
                        } else {
                                println("App version not found in DB, looking in folder...")
                                val appVersionInFolderDto =
                                        appService.getAppVersionByNameFromFolder(
                                                app.appName,
                                                appVersion
                                        )
                                if (appVersionInFolderDto.appVersion.isEmpty()) {
                                        throw IllegalArgumentException(
                                                "App version not found in folder"
                                        )
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
                println("Using app version: $appVersionDef")

                println("Creating new TestPlanVersion...")
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
                                executionTypeExecutionTypeId = request.executionTypeExecutionTypeId,
                                appPackage = request.appPackage,
                                mainActivity = request.mainActivity
                        )
                println("Saving new TestPlanVersion: $newVersion")
                val savedTestPlanVersionId = testPlanVersionRepository.save(newVersion)
                val savedTestPlanVersion =
                        newVersion.copy(testPlanVersionId = savedTestPlanVersionId)
                println("Saved TestPlanVersion")

                println("Mapping thresholds...")
                val testThreshold =
                        request.thresholds.map { threshold: TestThresholdRequestDTO ->
                                TestThreshold(
                                        testThresholdId = null,
                                        targetValue = threshold.targetValue,
                                        thresholdTypeThresholdTypeId =
                                                threshold.thresholdTypeThresholdTypeId,
                                        testPlanVersionTestPlanVersionId =
                                                savedTestPlanVersion.testPlanVersionId!!,
                                        metricOutputMetricOutputId =
                                                threshold.metricOutputMetricOutputId
                                )
                        }
                println("Saving thresholds: $testThreshold")
                val savedThresholds =
                        testThreshold.map { threshold ->
                                val id = testThresholdRepository.save(threshold)
                                threshold.copy(testThresholdId = id)
                        }
                println("Saved all thresholds")

                println("Mapping metric parameters...")
                val testMetricParameters =
                        request.metricParameters.map { parameter: TestMetricParameterRequestDTO ->
                                TestMetricParameter(
                                        testMetricParameterId = null,
                                        parameterValue = parameter.parameterValue,
                                        metricParameterMetricParameterId =
                                                parameter.metricParameterMetricParameterId,
                                        testPlanVersionTestPlanVersionId =
                                                savedTestPlanVersion.testPlanVersionId!!
                                )
                        }
                println("Saving metric parameters: $testMetricParameters")
                val savedMetricParameters =
                        testMetricParameters.map { parameter ->
                                val id = testPlanMetricParameterRepository.save(parameter)
                                parameter.copy(testMetricParameterId = id)
                        }
                println("Saved all metric parameters")

                println("Mapping execution type parameters...")
                val testExecutionTypeParameters =
                        request.executionTypeParameters.map {
                                parameter: TestExecutionTypeParameterRequestDTO ->
                                TestExecutionTypeParameter(
                                        testExecutionTypeParameterId = null,
                                        parameterValue = parameter.parameterValue,
                                        executionTypeParameterExecutionTypeParameterId =
                                                parameter
                                                        .executionTypeParameterExecutionTypeParameterId,
                                        testPlanVersionTestPlanVersionId =
                                                savedTestPlanVersion.testPlanVersionId!!
                                )
                        }
                println("Saving execution type parameters: $testExecutionTypeParameters")
                val savedExecutionTypeParameters =
                        testExecutionTypeParameters.map { parameter ->
                                val id = testPlanExecutionTypeParameterRepository.save(parameter)
                                parameter.copy(testExecutionTypeParameterId = id)
                        }
                println("Saved all execution type parameters")

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

                println("Returning TestPlanVersionResponseDTO")
                return TestPlanVersionResponseDTO(
                        testPlanVersionId = savedTestPlanVersion.testPlanVersionId,
                        version = savedTestPlanVersion.version,
                        creationTimestamp = savedTestPlanVersion.creationTimestamp,
                        notes = savedTestPlanVersion.notes,
                        testPlanTestPlanId = savedTestPlanVersion.testPlanTestPlanId,
                        deviceDeviceId = databaseDevice.deviceId,
                        appVersionAppVersionId = savedTestPlanVersion.appVersionAppVersionId,
                        appPackage = savedTestPlanVersion.appPackage,
                        mainActivity = savedTestPlanVersion.mainActivity,
                        executionTypeExecutionTypeId =
                                savedTestPlanVersion.executionTypeExecutionTypeId,
                        thresholds =
                                savedThresholds.map { threshold: TestThreshold ->
                                        TestThresholdResponseDTO(
                                                testThresholdId = threshold.testThresholdId!!,
                                                targetValue = threshold.targetValue,
                                                thresholdTypeThresholdTypeId =
                                                        threshold.thresholdTypeThresholdTypeId,
                                                testPlanVersionTestPlanVersionId =
                                                        threshold.testPlanVersionTestPlanVersionId,
                                                metricOutputMetricOutputId =
                                                        threshold.metricOutputMetricOutputId
                                        )
                                },
                        metricParameters =
                                savedMetricParameters.map { parameter: TestMetricParameter ->
                                        TestMetricParameterResponseDTO(
                                                testMetricParameterId =
                                                        parameter.testMetricParameterId!!,
                                                parameterValue = parameter.parameterValue,
                                                metricParameterMetricParameterId =
                                                        parameter.metricParameterMetricParameterId,
                                                testPlanVersionTestPlanVersionId =
                                                        parameter.testPlanVersionTestPlanVersionId
                                        )
                                },
                        executionTypeParameters =
                                savedExecutionTypeParameters.map {
                                        parameter: TestExecutionTypeParameter ->
                                        TestExecutionTypeParameterResponseDTO(
                                                testExecutionTypeParameterId =
                                                        parameter.testExecutionTypeParameterId!!,
                                                parameterValue = parameter.parameterValue,
                                                executionTypeParameterExecutionTypeParameterId =
                                                        parameter
                                                                .executionTypeParameterExecutionTypeParameterId,
                                                testPlanVersionTestPlanVersionId =
                                                        parameter.testPlanVersionTestPlanVersionId
                                        )
                                },
                        testSuiteVersionId = request.testSuiteVersionId
                )
        }
}