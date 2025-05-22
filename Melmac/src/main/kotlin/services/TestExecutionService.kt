package services

import domain.*
import domain.dtos.*
import repos.IRepos.*
import services.IServices.ITestExecutionService

class TestExecutionService(
        private val executionRepo: ITestExecutionRepository,
        private val metricRepo: IMetricRepository,
        private val typeRepo: IExecutionTypeRepository,
        private val suiteRepo: ITestSuiteRepository,
        private val appVersionRepo: IAppVersionRepository,
        private val execParamRepo: IExecutionTypeParameterRepository,
        private val metricParamRepo: IMetricParameterRepository,
        private val testExecParamRepo: ITestExecutionTypeParameterRepository,
        private val testMetricParamRepo: ITestMetricParameterRepository
) : ITestExecutionService {

        override fun create(request: TestExecutionRequestDTO): TestExecutionResponseDTO {
                val suite =
                        suiteRepo.findById(request.testSuiteId)
                                ?: throw IllegalArgumentException("Test suite not found")

                val appVersion =
                        appVersionRepo.findById(request.appVersionId)
                                ?: throw IllegalArgumentException("App version not found")

                val metric =
                        metricRepo.findById(request.metricId)
                                ?: throw IllegalArgumentException("Metric not found")

                val type =
                        typeRepo.findById(request.executionTypeId)
                                ?: throw IllegalArgumentException("Execution type not found")

                // Save TestExecution first
                val execution =
                        TestExecution(
                                testSuiteId = suite.id!!,
                                appVersionId = appVersion.id!!,
                                deviceId = request.deviceId,
                                metricId = metric.id!!,
                                executionTypeId = type.id!!
                        )
                val executionId = executionRepo.save(execution)

                // Validate and save execution parameters
                val allowedExecParams = execParamRepo.getAllByExecutionType(type.id)
                val executionParams =
                        request.executionParameters.map { dto ->
                                val param =
                                        allowedExecParams.find { it.id == dto.parameterId }
                                                ?: throw IllegalArgumentException(
                                                        "Invalid execution parameter ID: ${dto.parameterId}"
                                                )
                                TestExecutionTypeParameter(
                                        id = 0,
                                        executionId = executionId,
                                        executionTypeParameterId = param.id!!,
                                        value = dto.value
                                )
                        }
                testExecParamRepo.saveAll(executionParams)

                // Validate and save metric parameters
                val allowedMetricParams = metricParamRepo.findByMetricId(metric.id)
                val metricParams =
                        request.metricParameters.map { dto ->
                                val param =
                                        allowedMetricParams.find { it.id == dto.parameterId }
                                                ?: throw IllegalArgumentException(
                                                        "Invalid metric parameter ID: ${dto.parameterId}"
                                                )
                                TestMetricParameter(
                                        id = 0,
                                        testExecutionId = executionId,
                                        metricParameterId = param.id!!,
                                        value = dto.value
                                )
                        }
                testMetricParamRepo.saveAll(metricParams)

                // Build response
                return TestExecutionResponseDTO(
                        id = executionId,
                        testSuiteId = suite.id,
                        appVersionId = appVersion.id,
                        deviceId = execution.deviceId,
                        metricId = metric.id,
                        executionTypeId = type.id,
                        startTimestamp = execution.startTimestamp,
                        endTimestamp = execution.endTimestamp,
                        executionParameters =
                                executionParams.map {
                                        val def =
                                                allowedExecParams.find { p ->
                                                        p.id == it.executionTypeParameterId
                                                }!!
                                        ExecutionParameterValueResponseDTO(
                                                id = it.id,
                                                parameterId = def.id!!,
                                                name = def.name,
                                                value = it.value,
                                                type = def.type
                                        )
                                },
                        metricParameters =
                                metricParams.map {
                                        val def =
                                                allowedMetricParams.find { p ->
                                                        p.id == it.metricParameterId
                                                }!!
                                        MetricParameterValueResponseDTO(
                                                id = it.id,
                                                parameterId = def.id!!,
                                                name = def.name,
                                                value = it.value,
                                                type = def.type
                                        )
                                }
                )
        }

        override fun listAll(): List<TestExecutionResponseDTO> =
                executionRepo.findAll().map { toResponse(it) }

        override fun listBySuiteId(suiteId: Int): List<TestExecutionResponseDTO> =
                executionRepo.findBySuiteId(suiteId).map { toResponse(it) }

        private fun toResponse(exec: TestExecution): TestExecutionResponseDTO {
                val executionParams = testExecParamRepo.findByExecutionId(exec.id)
                val metricParams = testMetricParamRepo.findByExecutionId(exec.id)

                val execParamDefs = execParamRepo.getAllByExecutionType(exec.executionTypeId)
                val metricParamDefs = metricParamRepo.findByMetricId(exec.metricId)

                return TestExecutionResponseDTO(
                        id = exec.id,
                        testSuiteId = exec.testSuiteId,
                        appVersionId = exec.appVersionId,
                        deviceId = exec.deviceId,
                        metricId = exec.metricId,
                        executionTypeId = exec.executionTypeId,
                        startTimestamp = exec.startTimestamp,
                        endTimestamp = exec.endTimestamp,
                        executionParameters =
                                executionParams.map { ep ->
                                        val def =
                                                execParamDefs.find {
                                                        it.id == ep.executionTypeParameterId
                                                }!!
                                        ExecutionParameterValueResponseDTO(
                                                id = ep.id,
                                                parameterId = def.id!!,
                                                name = def.name,
                                                value = ep.value,
                                                type = def.type
                                        )
                                },
                        metricParameters =
                                metricParams.map { mp ->
                                        val def =
                                                metricParamDefs.find {
                                                        it.id == mp.metricParameterId
                                                }!!
                                        MetricParameterValueResponseDTO(
                                                id = mp.id,
                                                parameterId = def.id!!,
                                                name = def.name,
                                                value = mp.value,
                                                type = def.type
                                        )
                                }
                )
        }
}
