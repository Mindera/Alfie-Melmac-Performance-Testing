package services

import domain.*
import dtos.*
import repos.IRepos.*
import services.IServices.ITestSuiteService
import services.IServices.ITestExecutionService
import java.time.LocalDateTime
import java.time.LocalDate
import mappers.TestPlanVersionMapper
import mappers.TestThresholdMapper
import mappers.TestMetricParameterMapper
import mappers.TestExecutionTypeParameterMapper

/**
 * Service implementation for managing Test Suites.
 *
 * Provides methods to retrieve, create, and execute test suites, as well as
 * retrieve associated test plan versions.
 *
 * @property testSuiteRepository Repository for TestSuite entities.
 * @property testSuiteVersionRepository Repository for TestSuiteVersion entities.
 * @property testSuiteVersionPlanRepository Repository for TestSuiteVersionPlan entities.
 * @property testPlanVersionRepository Repository for TestPlanVersion entities.
 * @property testSuiteExecutionRepository Repository for SuiteExecution entities.
 * @property testExecutionService Service for executing test plans.
 * @property testThresholdRepository Repository for TestThreshold entities.
 * @property testMetricParameterRepository Repository for TestPlanMetricParameterValue entities.
 * @property testExecutionTypeParameterRepository Repository for TestPlanExecutionTypeParameterValue entities.
 */
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
    private val testPlanVersionMapper: TestPlanVersionMapper,
    private val testThresholdMapper: TestThresholdMapper,
    private val testMetricParameterMapper: TestMetricParameterMapper,
    private val testExecutionTypeParameterMapper: TestExecutionTypeParameterMapper
) : ITestSuiteService {

    /**
     * Retrieves all test suites.
     *
     * @return List of [TestSuiteResponseDTO] representing all test suites.
     */
    override fun getAllTestSuites(): List<TestSuiteResponseDTO> {
        return testSuiteRepository.findAll().map { it ->
            TestSuiteResponseDTO(
                testSuiteId = it.testSuiteId!!,
                testSuiteName = it.testSuiteName,
                testSuiteDescription = it.testSuiteDescription,
                creationTimestamp = it.creationTimestamp,
                isActive = it.isActive
            )
        }
    }

    /**
     * Retrieves a test suite by its ID.
     *
     * @param id The ID of the test suite.
     * @return [TestSuiteResponseDTO] for the specified test suite, or null if not found.
     */
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

    /**
     * Creates a new test suite and its initial version.
     *
     * @param request The [TestSuiteRequestDTO] containing test suite details.
     * @return [TestSuiteResponseDTO] representing the created test suite.
     */
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

    /**
     * Retrieves all test plan versions associated with the latest version of a test suite.
     *
     * @param suiteId The ID of the test suite.
     * @return List of [TestPlanVersionResponseDTO] for each associated test plan version.
     * @throws IllegalArgumentException if the test suite has no versions.
     */
    override fun getTestPlanVersionsBySuiteId(suiteId: Int): List<TestPlanVersionResponseDTO> {
        val latestVersion = testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId)
            ?: throw IllegalArgumentException("TestSuite $suiteId has no versions")
        val plans = testSuiteVersionPlanRepository.findByTestSuiteVersionId(latestVersion.testSuiteVersionId!!)
        return plans.mapNotNull { planLink: TestSuiteVersionPlan ->
            testPlanVersionRepository.findById(planLink.testPlanVersionTestPlanVersionId)?.let { planVersion: TestPlanVersion ->
                val thresholds = planVersion.testPlanVersionId?.let { id ->
                    testThresholdRepository.findByTestPlanVersionId(id)
                        .map { testThresholdMapper.toDto(it) }
                } ?: emptyList()
                val metricParameters = planVersion.testPlanVersionId?.let { id ->
                    testMetricParameterRepository.findByTestPlanVersionId(id)
                        .map { testMetricParameterMapper.toDto(it) }
                } ?: emptyList()
                val executionTypeParameters = planVersion.testPlanVersionId?.let { id ->
                    testExecutionTypeParameterRepository.findByTestPlanVersionId(id)
                        .map { testExecutionTypeParameterMapper.toDto(it) }
                } ?: emptyList()

                testPlanVersionMapper.toDto(
                    planVersion,
                    thresholds,
                    metricParameters,
                    executionTypeParameters,
                    latestVersion.testSuiteVersionId
                )
            }
        }
    }

    /**
     * Executes all test plans associated with the latest version of a test suite.
     *
     * @param suiteId The ID of the test suite.
     * @return [SuiteExecutionResponseDTO] containing the results of the suite execution.
     * @throws IllegalArgumentException if the test suite has no versions.
     * @throws IllegalStateException if the test suite version has no test plans.
     */
    override fun runTestSuiteExecution(suiteId: Int): SuiteExecutionResponseDTO {
        val latestVersion = testSuiteVersionRepository.findLatestVersionByTestSuiteId(suiteId)
            ?: throw IllegalArgumentException("TestSuite $suiteId has no versions")

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
            initialTimestamp = startTime,
            endTimestamp = endTime,
            testSuiteVersionTestSuiteVersionId = latestVersion.testSuiteVersionId,
            executionResults = executionResults
        )
    }
}