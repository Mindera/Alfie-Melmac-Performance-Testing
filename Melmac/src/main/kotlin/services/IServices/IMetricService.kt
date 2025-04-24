package services.IServices

import domain.dtos.*

interface IMetricService {

    fun getAllMetrics(): List<MetricResponseDTO>

    fun getMetricById(id: Int): MetricResponseDTO?

    fun getOutputsByMetricId(metricId: Int): List<OutputResponseDTO>

    fun getExecutionTypesByMetricId(metricId: Int): List<ExecutionTypeResponseDTO>

    fun getMetricParametersByMetricId(metricId: Int): List<MetricParameterResponseDTO>

    fun loadFromJson()
}
