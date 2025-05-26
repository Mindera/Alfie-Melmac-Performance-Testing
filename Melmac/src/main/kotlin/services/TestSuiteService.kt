package services

import domain.*
import dtos.*
import repos.IRepos.*
import services.IServices.ITestSuiteService
import services.IServices.ITestExecutionService
import java.time.LocalDateTime

class TestSuiteService(
    private val testSuiteRepository: ITestSuiteRepository,
    private val testSuiteVersionRepository: ITestSuiteVersionRepository,
    private val testSuiteVersionPlanRepository: ITestSuiteVersionPlanRepository,
    private val testPlanVersionRepository: ITestPlanVersionRepository,
    private val testSuiteExecutionRepository: ITestSuiteExecutionRepository,
    private val testExecutionService: ITestExecutionService,
    private val testThresholdRepository: IThresholdRepository,
    private val testMetricParameterRepository: ITestPlanMetricParameterValueRepository,
    private val testExecutionTypeParameterRepository: ITestPlanExecutionTypeParameterValueRepository,
) : ITestSuiteService {

    override fun getAllTestSuites(): List<TestSuiteResponseDTO> {
        return testSuiteRepository.findAll().map {it ->
            TestSuiteResponseDTO(
                testSuiteId = it.testSuiteId!!,
                testSuiteName = it.testSuiteName, 
                testSuiteDescription = it.testSuiteDescription,
                creationTimestamp = it.creationTimestamp,
                isActive = it.isActive
            )
        }
    }

    override fun getTestSuiteById(id: Int): TestSuiteResponseDTO? {
        val testSuite = testSuiteRepository.findById(id) ?: return null
        return TestSuiteResponseDTO(
            testSuiteId = testSuite.testSuiteId!!,
            testSuiteName = testSuite.testSuiteName,
            testSuiteDescription = testSuite.testSuiteDescription,
            creationTimestamp = testSuite.creationTimestamp,
            isActive = testSuite.isActive
        )
    }

    override fun createTestSuite(request: TestSuiteRequestDTO): TestSuiteResponseDTO {
        val newSuite = TestSuite(
            testSuiteId = null,
            testSuiteName = request.testSuiteName,
            testSuiteDescription = request.testSuiteDescription,
            creationTimestamp = LocalDateTime.now(),
            isActive = true
        )
        val newSuiteId = testSuiteRepository.save(newSuite)

        val newVersion = TestSuiteVersion(
            testSuiteVersionId = null,
            version = ((testSuiteRepository.findLatestVersionByTestSuiteId(newSuiteId) ?: 0) + 1).toString(),
            creationTimestamp = LocalDateTime.now(),
            notes = null,
            testSuiteTestSuiteId = newSuiteId
        )
        testSuiteVersionRepository.save(newVersion)

        return TestSuiteResponseDTO(
            testSuiteId = newSuiteId,
            testSuiteName = request.testSuiteName,
            testSuiteDescription = request.testSuiteDescription,
            creationTimestamp = newVersion.creationTimestamp,
            isActive = true
        )
    }

    override fun getTestPlanVersionsBySuiteId(suiteId: Int): List<TestPlanVersionResponseDTO> {
        val latestVersion = testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId)
        if (latestVersion == null) {
            throw IllegalArgumentException("TestSuite $suiteId has no versions")
        }
        val plans = testSuiteVersionPlanRepository.findByTestSuiteVersionId(latestVersion.testSuiteVersionId!!)
        return plans.mapNotNull { planLink: TestSuiteVersionPlan ->
            testPlanVersionRepository.findById(planLink.testPlanVersionTestPlanVersionId)?.let { planVersion: TestPlanVersion ->
                val thresholds = planVersion.testPlanVersionId?.let { id ->
                    testThresholdRepository.findByTestPlanVersionId(id)
                } ?: emptyList()
                val metricParameters = planVersion.testPlanVersionId?.let { id ->
                    testMetricParameterRepository.findByTestPlanVersionId(id)
                } ?: emptyList()
                val executionTypeParameters = planVersion.testPlanVersionId?.let { id ->
                    testExecutionTypeParameterRepository.findByTestPlanVersionId(id)
                } ?: emptyList()

                TestPlanVersionResponseDTO(
                    testPlanVersionId = planVersion.testPlanVersionId!!,
                    version = planVersion.version,
                    creationTimestamp = planVersion.creationTimestamp,
                    notes = planVersion.notes,
                    testPlanTestPlanId = planVersion.testPlanTestPlanId,
                    deviceDeviceId = planVersion.deviceDeviceId,
                    appVersionAppVersionId = planVersion.appVersionAppVersionId,
                    appPackage = planVersion.appPackage,
                    mainActivity = planVersion.mainActivity,
                    executionTypeExecutionTypeId = planVersion.executionTypeExecutionTypeId,
                    thresholds = thresholds.map { threshold: TestThreshold ->
                        TestThresholdResponseDTO(
                            testThresholdId = threshold.testThresholdId ?: 0,
                            targetValue = threshold.targetValue,
                            thresholdTypeThresholdTypeId = threshold.thresholdTypeThresholdTypeId,
                            testPlanVersionTestPlanVersionId = threshold.testPlanVersionTestPlanVersionId,
                            metricOutputMetricOutputId = threshold.metricOutputMetricOutputId
                        )
                    },
                    metricParameters = metricParameters.map { parameter: TestMetricParameter ->
                        TestMetricParameterResponseDTO(
                            testMetricParameterId = parameter.testMetricParameterId ?: 0,
                            parameterValue = parameter.parameterValue,
                            metricParameterMetricParameterId = parameter.metricParameterMetricParameterId,
                            testPlanVersionTestPlanVersionId = parameter.testPlanVersionTestPlanVersionId
                        )
                    },
                    executionTypeParameters = executionTypeParameters.map { parameter: TestExecutionTypeParameter ->
                        TestExecutionTypeParameterResponseDTO(
                            testExecutionTypeParameterId = parameter.testExecutionTypeParameterId ?: 0,
                            parameterValue = parameter.parameterValue,
                            executionTypeParameterExecutionTypeParameterId = parameter.executionTypeParameterExecutionTypeParameterId,
                            testPlanVersionTestPlanVersionId = parameter.testPlanVersionTestPlanVersionId
                        )
                    }
                )
            }
        }
    }

    override fun runTestSuiteExecution(suiteId: Int): SuiteExecutionResponseDTO {
        val latestVersion = testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId)
        if (latestVersion == null) {
            throw IllegalArgumentException("TestSuite $suiteId has no versions")
        }

        val suitePlans = testSuiteVersionPlanRepository.findByTestSuiteVersionId(latestVersion.testSuiteVersionId!!)
        if (suitePlans.isEmpty()) {
            throw IllegalStateException("TestSuiteVersion ${latestVersion.testSuiteVersionId} has no TestPlans")
        }

        val startTime = LocalDateTime.now()

        val executionResults = suitePlans.map { planLink: TestSuiteVersionPlan ->
            testExecutionService.runTestExecution(planLink.testPlanVersionTestPlanVersionId)
        }

        val endTime = LocalDateTime.now()

        val suiteExecution = SuiteExecution(
            suiteExecutionId = null,
            initialTimestamp = startTime,
            endTimestamp = endTime,
            testSuiteVersionTestSuiteVersionId = latestVersion.testSuiteVersionId
        )
        val suiteExecutionId = testSuiteExecutionRepository.save(suiteExecution)

        return SuiteExecutionResponseDTO(
            suiteExecutionId = suiteExecutionId,
            initialTimestamp = LocalDateTime.now(),
            endTimestamp = LocalDateTime.now(),
            testSuiteVersionTestSuiteVersionId = latestVersion.testSuiteVersionId,
            executionResults = executionResults.map { result: TestExecutionResponseDTO ->
                TestExecutionResponseDTO(
                    testExecutionId = result.testExecutionId,
                    initialTimestamp = LocalDateTime.now(),
                    endTimestamp = LocalDateTime.now(),
                    passed = if (result.passed == "true" || result.passed == "false") result.passed else "false",
                    testPlanVersionTestPlanVersionId = result.testPlanVersionTestPlanVersionId
                )
            }
        )
    }
}

