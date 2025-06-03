package services

import domain.*
import dtos.*
import repos.IRepos.*
import services.IServices.IMetricService

/**
 * Service implementation for managing metrics and their related entities.
 * Provides methods to retrieve metrics, outputs, parameters, and execution types.
 *
 * @property metricRepository Repository for Metric entities.
 * @property metricOutputRepository Repository for MetricOutput entities.
 * @property metricParameterRepository Repository for MetricParameter entities.
 * @property executionTypeRepository Repository for ExecutionType entities.
 * @property executionTypeParameterRepository Repository for ExecutionTypeParameter entities.
 */
class MetricService(
        private val metricRepository: IMetricRepository,
        private val metricOutputRepository: IMetricOutputRepository,
        private val metricParameterRepository: IMetricParameterRepository,
        private val executionTypeRepository: IExecutionTypeRepository,
        private val executionTypeParameterRepository: IExecutionTypeParameterRepository
) : IMetricService {

    /**
     * Retrieves all metrics.
     *
     * @return List of [MetricResponseDTO] representing all metrics.
     */
    override fun getAllMetrics(): List<MetricResponseDTO> {
        return metricRepository.findAll().map { metric ->
            MetricResponseDTO(
                    metricId = metric.metricId
                                    ?: throw IllegalStateException("Metric ID cannot be null"),
                    metricName = metric.metricName
            )
        }
    }

    /**
     * Retrieves a metric by its ID.
     *
     * @param id The ID of the metric.
     * @return [MetricResponseDTO] for the metric, or null if not found.
     */
    override fun getMetricById(id: Int): MetricResponseDTO? {
        val metric = metricRepository.findById(id) ?: return null
        return MetricResponseDTO(
                metricId = metric.metricId
                                ?: throw IllegalStateException("Metric ID cannot be null"),
                metricName = metric.metricName
        )
    }

    /**
     * Retrieves all outputs for a given metric ID.
     *
     * @param metricId The ID of the metric.
     * @return List of [MetricOutputResponseDTO] for the metric.
     */
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

    /**
     * Retrieves all parameters for a given metric ID.
     *
     * @param metricId The ID of the metric.
     * @return List of [MetricParameterResponseDTO] for the metric.
     */
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

    /**
     * Retrieves all execution types for a given metric ID.
     *
     * @param metricId The ID of the metric.
     * @return List of [ExecutionTypeResponseDTO] for the metric.
     */
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

    /**
     * Retrieves all parameters for a given execution type ID.
     *
     * @param executionTypeId The ID of the execution type.
     * @return List of [ExecutionTypeParameterResponseDTO] for the execution type.
     */
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