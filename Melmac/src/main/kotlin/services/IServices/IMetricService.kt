package services.IServices

import dtos.*

/**
 * Service interface for managing metrics and their associated data.
 * Provides methods to retrieve metric information, including outputs, parameters, and execution types.
 */
interface IMetricService {
    fun getAllMetrics(): List<MetricResponseDTO>
    fun getMetricById(id: Int): MetricResponseDTO?
    fun getOutputsByMetricId(metricId: Int): List<MetricOutputResponseDTO>
    fun getParametersByMetricId(metricId: Int): List<MetricParameterResponseDTO>
    fun getExecutionTypesByMetricId(metricId: Int): List<ExecutionTypeResponseDTO>
    fun getParametersByExecutionTypeId(executionTypeId: Int): List<ExecutionTypeParameterResponseDTO>
}

