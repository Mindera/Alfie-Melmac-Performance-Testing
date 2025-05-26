package services.IServices

import dtos.*

interface IMetricService {
    fun getAllMetrics(): List<MetricResponseDTO>
    fun getMetricById(id: Int): MetricResponseDTO?
    fun getOutputsByMetricId(metricId: Int): List<MetricOutputResponseDTO>
    fun getParametersByMetricId(metricId: Int): List<MetricParameterResponseDTO>
    fun getExecutionTypesByMetricId(metricId: Int): List<ExecutionTypeResponseDTO>
    fun getParametersByExecutionTypeId(executionTypeId: Int): List<ExecutionTypeParameterResponseDTO>
}

