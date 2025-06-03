package services

import domain.*
import dtos.*
import repos.IRepos.*
import services.IServices.ITestPlanVersionService
import mappers.TestThresholdMapper
import mappers.TestMetricParameterMapper
import mappers.TestExecutionTypeParameterMapper
import mappers.TestPlanVersionMapper

/**
 * Service implementation for managing Test Plan Versions.
 *
 * Provides methods to retrieve test plan versions, including their thresholds,
 * metric parameters, execution type parameters, and associated test suite version IDs.
 *
 * @property testPlanVersionRepository Repository for TestPlanVersion entities.
 * @property testThresholdRepository Repository for TestThreshold entities.
 * @property testPlanMetricParameterRepository Repository for TestPlanMetricParameterValue entities.
 * @property testPlanExecutionTypeParameterRepository Repository for TestPlanExecutionTypeParameterValue entities.
 * @property testSuiteVersionPlanRepository Repository for TestSuiteVersionPlan entities.
 */
class TestPlanVersionService(
        private val testPlanVersionRepository: ITestPlanVersionRepository,
        private val testThresholdRepository: IThresholdRepository,
        private val testPlanMetricParameterRepository: ITestPlanMetricParameterValueRepository,
        private val testPlanExecutionTypeParameterRepository:
                ITestPlanExecutionTypeParameterValueRepository,
        private val testSuiteVersionPlanRepository: ITestSuiteVersionPlanRepository,
) : ITestPlanVersionService {

    /**
     * Retrieves all versions of a test plan by its ID, including thresholds,
     * metric parameters, execution type parameters, and test suite version ID.
     *
     * @param id The ID of the test plan.
     * @return List of [TestPlanVersionResponseDTO] representing each version.
     */
    override fun getTestPlanVersionsByTestPlanId(id: Int): List<TestPlanVersionResponseDTO> {
        val versions = testPlanVersionRepository.findByTestPlanId(id)
        return versions.map { version: TestPlanVersion ->
            val thresholds =
                testThresholdRepository.findByTestPlanVersionId(version.testPlanVersionId!!)
                    .map { threshold -> TestThresholdMapper.toDto(threshold) }

            val metricParameters =
                testPlanMetricParameterRepository.findByTestPlanVersionId(version.testPlanVersionId)
                    .map { parameter -> TestMetricParameterMapper.toDto(parameter) }

            val executionTypeParameters =
                testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(version.testPlanVersionId)
                    .map { parameter -> TestExecutionTypeParameterMapper.toDto(parameter) }

            val testSuiteVersionId =
                testSuiteVersionPlanRepository
                    .findByTestPlanVersionId(version.testPlanVersionId)
                    .firstOrNull()
                    ?.testSuiteVersionTestSuiteVersionId
                    ?: 0

            TestPlanVersionMapper.toDto(
                version,
                thresholds,
                metricParameters,
                executionTypeParameters,
                testSuiteVersionId
            )
        }
    }

    /**
     * Retrieves the latest version of a test plan by its ID, including thresholds,
     * metric parameters, execution type parameters, and test suite version ID.
     *
     * @param testPlanId The ID of the test plan.
     * @return [TestPlanVersionResponseDTO] for the latest version, or null if not found.
     */
    override fun getLatestTestPlanVersionByTestPlanId(
            testPlanId: Int
    ): TestPlanVersionResponseDTO? {
        val version =
            testPlanVersionRepository.findLatestVersionByTestPlanId(testPlanId) ?: return null

        val thresholds =
            testThresholdRepository.findByTestPlanVersionId(version.testPlanVersionId!!)
                .map { threshold -> TestThresholdMapper.toDto(threshold) }

        val metricParameters =
            testPlanMetricParameterRepository.findByTestPlanVersionId(version.testPlanVersionId)
                .map { parameter -> TestMetricParameterMapper.toDto(parameter) }

        val executionTypeParameters =
            testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(version.testPlanVersionId)
                .map { parameter -> TestExecutionTypeParameterMapper.toDto(parameter) }

        val testSuiteVersionId =
            testSuiteVersionPlanRepository
                .findByTestPlanVersionId(version.testPlanVersionId)
                .firstOrNull()
                ?.testSuiteVersionTestSuiteVersionId
                ?: 0

        return TestPlanVersionMapper.toDto(
            version,
            thresholds,
            metricParameters,
            executionTypeParameters,
            testSuiteVersionId
        )
    }

    /**
     * Retrieves a test plan version by its ID, including thresholds,
     * metric parameters, execution type parameters, and test suite version ID.
     *
     * @param id The ID of the test plan version.
     * @return [TestPlanVersionResponseDTO] for the specified version, or null if not found.
     */
    override fun getTestPlanVersionById(id: Int): TestPlanVersionResponseDTO? {
        val version = testPlanVersionRepository.findById(id) ?: return null

        val thresholds =
            testThresholdRepository.findByTestPlanVersionId(version.testPlanVersionId!!)
                .map { threshold -> TestThresholdMapper.toDto(threshold) }

        val metricParameters =
            testPlanMetricParameterRepository.findByTestPlanVersionId(version.testPlanVersionId)
                .map { parameter -> TestMetricParameterMapper.toDto(parameter) }

        val executionTypeParameters =
            testPlanExecutionTypeParameterRepository.findByTestPlanVersionId(version.testPlanVersionId)
                .map { parameter -> TestExecutionTypeParameterMapper.toDto(parameter) }

        val testSuiteVersionId =
            testSuiteVersionPlanRepository
                .findByTestPlanVersionId(version.testPlanVersionId)
                .firstOrNull()
                ?.testSuiteVersionTestSuiteVersionId
                ?: 0

        return TestPlanVersionMapper.toDto(
            version,
            thresholds,
            metricParameters,
            executionTypeParameters,
            testSuiteVersionId
        )
    }
}