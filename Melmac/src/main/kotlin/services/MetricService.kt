package services

import domain.*
import dtos.*
import repos.IRepos.*
import services.IServices.IMetricService

class MetricService(
        private val metricRepository: IMetricRepository,
        private val metricOutputRepository: IMetricOutputRepository,
        private val metricParameterRepository: IMetricParameterRepository,
        private val executionTypeRepository: IExecutionTypeRepository,
        private val executionTypeParameterRepository: IExecutionTypeParameterRepository
) : IMetricService {

    override fun getAllMetrics(): List<MetricResponseDTO> {
        return metricRepository.findAll().map { metric ->
            MetricResponseDTO(
                    metricId = metric.metricId
                                    ?: throw IllegalStateException("Metric ID cannot be null"),
                    metricName = metric.metricName
            )
        }
    }

    override fun getMetricById(id: Int): MetricResponseDTO? {
        val metric = metricRepository.findById(id) ?: return null
        return MetricResponseDTO(
                metricId = metric.metricId
                                ?: throw IllegalStateException("Metric ID cannot be null"),
                metricName = metric.metricName
        )
    }

    override fun getOutputsByMetricId(metricId: Int): List<MetricOutputResponseDTO> {
        return metricOutputRepository.findByMetricId(metricId).map { output: MetricOutput ->
            MetricOutputResponseDTO(
                    if (output.metricOutputId == null) {
                        throw IllegalStateException("MetricOutput ID cannot be null")
                    } else {
                        output.metricOutputId
                    },
                    output.outputName,
                    output.unit,
                    output.metricMetricId
            )
        }
    }

    override fun getParametersByMetricId(metricId: Int): List<MetricParameterResponseDTO> {
        return metricParameterRepository.findByMetricId(metricId).map { param ->
            MetricParameterResponseDTO(
                    metricParameterId = param.metricParameterId
                                    ?: throw IllegalStateException(
                                            "MetricParameter ID cannot be null"
                                    ),
                    parameterName = param.parameterName,
                    parameterType = param.parameterType,
                    metricMetricId = param.metricMetricId
            )
        }
    }

    override fun getExecutionTypesByMetricId(metricId: Int): List<ExecutionTypeResponseDTO> {
        return executionTypeRepository.findByMetricId(metricId).map { execType: ExecutionType ->
            ExecutionTypeResponseDTO(
                    if (execType.executionTypeId == null) {
                        throw IllegalStateException("ExecutionType ID cannot be null")
                    } else {
                        execType.executionTypeId
                    },
                    execType.executionTypeName,
                    execType.executionTypeDescription
            )
        }
    }

    override fun getParametersByExecutionTypeId(
            executionTypeId: Int
    ): List<ExecutionTypeParameterResponseDTO> {
        return executionTypeParameterRepository.findByExecutionTypeId(executionTypeId).map {
                param: ExecutionTypeParameter ->
            ExecutionTypeParameterResponseDTO(
                    if (param.executionTypeParameterId == null) {
                        throw IllegalStateException("ExecutionTypeParameter ID cannot be null")
                    } else {
                        param.executionTypeParameterId
                    },
                    param.parameterName,
                    param.parameterType,
                    param.executionTypeExecutionTypeId
            )
        }
    }
}
