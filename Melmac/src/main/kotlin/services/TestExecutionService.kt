package services

import core.runners.ITestRunner
import domain.*
import dtos.TestExecutionConfigDTO
import dtos.TestExecutionResponseDTO
import java.time.Instant
import java.time.ZoneId
import repos.IRepos.*
import services.IServices.ITestExecutionService

/**
 * Service implementation for managing test executions.
 * Handles retrieval, execution, and result storage for test executions.
 *
 * @property testExecutionRepository Repository for TestExecution entities.
 * @property testPlanVersionRepository Repository for TestPlanVersion entities.
 * @property metricRepository Repository for Metric entities.
 * @property metricOutputRepository Repository for MetricOutput entities.
 * @property executionTypeRepository Repository for ExecutionType entities.
 * @property deviceRepository Repository for Device entities.
 * @property osVersionRepository Repository for OS version entities.
 * @property osRepository Repository for OS entities.
 * @property appVersionRepository Repository for AppVersion entities.
 * @property appRepository Repository for App entities.
 * @property testPlanExecutionTypeParamValueRepo Repository for ExecutionTypeParameter values.
 * @property testPlanMetricParamValueRepo Repository for MetricParameter values.
 * @property testPlanRepository Repository for TestPlan entities.
 * @property metricParameterRepository Repository for MetricParameter entities.
 * @property executionTypeParameterRepository Repository for ExecutionTypeParameter entities.
 * @property testThresholdRepository Repository for Threshold entities.
 * @property thresholdTypeRepository Repository for ThresholdType entities.
 * @property testMetricOutputResultRepository Repository for TestMetricOutputResult entities.
 * @property testRunner Test runner interface for executing tests.
 */
class TestExecutionService(
        private val testExecutionRepository: ITestExecutionRepository,
        private val testPlanVersionRepository: ITestPlanVersionRepository,
        private val metricRepository: IMetricRepository,
        private val metricOutputRepository: IMetricOutputRepository,
        private val executionTypeRepository: IExecutionTypeRepository,
        private val deviceRepository: IDeviceRepository,
        private val osVersionRepository: IOperSysVersionRepository,
        private val osRepository: IOperSysRepository,
        private val appVersionRepository: IAppVersionRepository,
        private val appRepository: IAppRepository,
        private val testPlanExecutionTypeParamValueRepo:
                ITestPlanExecutionTypeParameterValueRepository,
        private val testPlanMetricParamValueRepo: ITestPlanMetricParameterValueRepository,
        private val testPlanRepository: ITestPlanRepository,
        private val metricParameterRepository: IMetricParameterRepository,
        private val executionTypeParameterRepository: IExecutionTypeParameterRepository,
        private val testThresholdRepository: IThresholdRepository,
        private val thresholdTypeRepository: IThresholdTypeRepository,
        private val testMetricOutputResultRepository: ITestMetricOutputResultRepository,
        private val testRunner: ITestRunner
) : ITestExecutionService {

        /**
         * Retrieves all test executions.
         *
         * @return List of [TestExecutionResponseDTO] representing all test executions.
         */
        override fun getAllTestExecutions(): List<TestExecutionResponseDTO> {
                return testExecutionRepository.findAll().map {
                        TestExecutionResponseDTO(
                                testExecutionId = it.testExecutionId!!,
                                initialTimestamp = it.initialTimestamp,
                                endTimestamp = it.endTimestamp,
                                passed = it.passed,
                                testPlanVersionTestPlanVersionId =
                                        it.testPlanVersionTestPlanVersionId
                        )
                }
        }

        /**
         * Retrieves a test execution by its ID.
         *
         * @param id The ID of the test execution.
         * @return [TestExecutionResponseDTO] for the test execution, or null if not found.
         */
        override fun getTestExecutionById(id: Int): TestExecutionResponseDTO? {
                val execution = testExecutionRepository.findById(id) ?: return null
                return TestExecutionResponseDTO(
                        testExecutionId = execution.testExecutionId!!,
                        initialTimestamp = execution.initialTimestamp,
                        endTimestamp = execution.endTimestamp,
                        passed = execution.passed,
                        testPlanVersionTestPlanVersionId =
                                execution.testPlanVersionTestPlanVersionId
                )
        }

        /**
         * Executes a test plan version and stores the results.
         *
         * @param testPlanVersionId The ID of the test plan version to execute.
         * @return [TestExecutionResponseDTO] containing the execution result.
         * @throws IllegalStateException if any required entity is not found.
         */
        override fun runTestExecution(testPlanVersionId: Int): TestExecutionResponseDTO {
                val testPlanVersion =
                        testPlanVersionRepository.findById(testPlanVersionId)
                                ?: throw IllegalStateException(
                                        "TestPlanVersion with ID $testPlanVersionId not found"
                                )

                val testPlan =
                        testPlanRepository.findById(testPlanVersion.testPlanTestPlanId)
                                ?: throw IllegalStateException(
                                        "TestPlan ${testPlanVersion.testPlanTestPlanId} not found"
                                )

                val metric =
                        metricRepository.findById(testPlan.metricMetricId)
                                ?: throw IllegalStateException(
                                        "Metric ${testPlan.metricMetricId} not found"
                                )

                val executionType =
                        executionTypeRepository.findById(
                                testPlanVersion.executionTypeExecutionTypeId
                        )
                                ?: throw IllegalStateException(
                                        "ExecutionType ${testPlanVersion.executionTypeExecutionTypeId} not found"
                                )

                val device =
                        deviceRepository.findById(testPlanVersion.deviceDeviceId)
                                ?: throw IllegalStateException(
                                        "Device ${testPlanVersion.deviceDeviceId} not found"
                                )

                val os =
                        osRepository.findById(device.osVersionOsVersionId)
                                ?: throw IllegalStateException(
                                        "OS ${device.osVersionOsVersionId} not found"
                                )

                val appVersion =
                        appVersionRepository.findById(testPlanVersion.appVersionAppVersionId)
                                ?: throw IllegalStateException(
                                        "AppVersion ${testPlanVersion.appVersionAppVersionId} not found"
                                )

                val app =
                        appRepository.findById(appVersion.appId)
                                ?: throw IllegalStateException("App ${appVersion.appId} not found")

                val execTypeParamsValues =
                        testPlanExecutionTypeParamValueRepo.findByTestPlanVersionId(
                                testPlanVersionId
                        )
                val metricParamsValues =
                        testPlanMetricParamValueRepo.findByTestPlanVersionId(testPlanVersionId)

                val execTypeParams =
                        execTypeParamsValues.mapNotNull { paramValue ->
                                executionTypeParameterRepository.findById(
                                        paramValue.executionTypeParameterExecutionTypeParameterId
                                )
                        }

                val metricParams =
                        metricParamsValues.mapNotNull { paramValue ->
                                metricParameterRepository.findById(
                                        paramValue.metricParameterMetricParameterId
                                )
                        }

                val metricOutputs =
                        metricOutputRepository.findByMetricId(
                                metric.metricId
                                        ?: throw IllegalStateException("Metric ID cannot be null")
                        )

                val thresholds =
                        testThresholdRepository.findByTestPlanVersionId(testPlanVersionId).map {
                                threshold ->
                                val thresholdType =
                                        thresholdTypeRepository.findById(
                                                threshold.thresholdTypeThresholdTypeId
                                        )
                                                ?: throw IllegalStateException(
                                                        "ThresholdType ${threshold.thresholdTypeThresholdTypeId} not found"
                                                )
                                val metricOutput =
                                        metricOutputs.find {
                                                it.metricOutputId ==
                                                        threshold.metricOutputMetricOutputId
                                        }
                                                ?: throw IllegalStateException(
                                                        "MetricOutput ${threshold.metricOutputMetricOutputId} not found"
                                                )
                                Triple(
                                        threshold.targetValue.toString(),
                                        thresholdType.thresholdTypeName,
                                        metricOutput.outputName ?: ""
                                )
                        }

                val configDTO =
                        TestExecutionConfigDTO(
                                executionTypeName = executionType.executionTypeName,
                                metricName = metric.metricName,
                                metricParams =
                                        metricParams.associate { metricParam ->
                                                val paramValue =
                                                        metricParamsValues.firstOrNull {
                                                                it.metricParameterMetricParameterId ==
                                                                        metricParam
                                                                                .metricParameterId
                                                        }
                                                metricParam.parameterName to
                                                        (paramValue?.parameterValue ?: "")
                                        },
                                executionTypeParams =
                                        execTypeParams.associate { execTypeParam ->
                                                val paramValue =
                                                        execTypeParamsValues.firstOrNull {
                                                                it.executionTypeParameterExecutionTypeParameterId ==
                                                                        execTypeParam
                                                                                .executionTypeParameterId
                                                        }
                                                execTypeParam.parameterName to
                                                        (paramValue?.parameterValue ?: "")
                                        },
                                testThresholds =
                                        thresholds.takeIf { it.isNotEmpty() }?.map { triple ->
                                                Triple(
                                                        triple.first,
                                                        triple.second,
                                                        triple.third
                                                )
                                        },
                                deviceName = device.deviceName,
                                deviceSerialNumber = device.deviceSerialNumber,
                                platform = os.operSysName,
                                appName = app.appName,
                                appVersion = appVersion.appVersion,
                                appPackage = testPlanVersion.appPackage,
                                mainActivity = testPlanVersion.mainActivity,
                        )

                val start =
                        Instant.ofEpochMilli(System.currentTimeMillis())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()

                // Call the test runner and expect it to return a map with at least "success" and
                // "value"
                val resultOutputs = testRunner.run(configDTO)

                val end =
                        Instant.ofEpochMilli(System.currentTimeMillis())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()

                // Use the "success" field from the test output, default to false if missing
                val passed = resultOutputs["success"]?.toBoolean() ?: false

                val testExecution =
                        TestExecution(
                                testExecutionId = null,
                                testPlanVersionTestPlanVersionId = testPlanVersion.testPlanVersionId
                                                ?: throw IllegalStateException(
                                                        "TestPlanVersionId cannot be null"
                                                ),
                                initialTimestamp = start,
                                endTimestamp = end,
                                passed = passed.toString()
                        )
                val testExecutionId = testExecutionRepository.save(testExecution)

                (metricOutputs as Iterable<MetricOutput>).forEach { output ->
                        val outputId =
                                output.metricOutputId
                                        ?: throw IllegalStateException(
                                                "MetricOutput ID cannot be null"
                                        )
                        val value = resultOutputs[output.outputName] ?: ""
                        testMetricOutputResultRepository.save(
                                TestMetricOutputResult(
                                        testMetricOutputResultId = null,
                                        testExecutionTestExecutionId = testExecutionId,
                                        metricOutputMetricOutputId = outputId,
                                        value = value,
                                )
                        )
                }

                return TestExecutionResponseDTO(
                        testExecutionId = testExecutionId,
                        initialTimestamp = testExecution.initialTimestamp,
                        endTimestamp = testExecution.endTimestamp,
                        passed = testExecution.passed,
                        testPlanVersionTestPlanVersionId =
                                testExecution.testPlanVersionTestPlanVersionId
                )
        }
}