package services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import domain.*
import domain.dtos.*
import repos.IRepos.*
import services.IServices.IMetricService

class MetricService(
    private val repository: IMetricRepository,
    private val outputRepo: IOutputRepository,
    private val executionTypeRepo: IExecutionTypeRepository,
    private val executionTypeParamRepo: IExecutionTypeParameterRepository,
    private val executionTypeMetricRepo: IExecutionTypeMetricRepository,
    private val parameterRepo: IMetricParameterRepository
) : IMetricService {

    override fun getAllMetrics(): List<MetricResponseDTO> =
        repository.findAll().map { it.toDTO() }

    override fun getMetricById(id: Int): MetricResponseDTO? =
        repository.findById(id)?.toDTO()

    override fun getOutputsByMetricId(metricId: Int): List<OutputResponseDTO> =
        outputRepo.findByMetricId(metricId).map { it.toDTO() }

    override fun getExecutionTypesByMetricId(metricId: Int): List<ExecutionTypeResponseDTO> {
        val executionTypeIds = executionTypeMetricRepo.getExecutionTypesForMetric(metricId)
        return executionTypeIds.mapNotNull { executionTypeRepo.findById(it) }.map { it.toDTO() }
    }

    override fun getMetricParametersByMetricId(metricId: Int): List<MetricParameterResponseDTO> =
        parameterRepo.findByMetricId(metricId).map { it.toDTO() }

    override fun loadFromJson() {
        val path = "src/main/resources/metrics.json"
        val mapper = jacksonObjectMapper()
        val file = java.io.File(path)

        if (!file.exists()) {
            println("⚠️  Config file not found: $path")
            return
        }

        val config = mapper.readValue<MetricConfigFileDTO>(file)

        config.metrics.forEach { metricDTO ->
            val existingMetric = repository.findByName(metricDTO.name)
            val metricId = existingMetric?.id ?: repository.save(
                Metric(id = null, name = metricDTO.name, description = metricDTO.description)
            )

            // Outputs
            metricDTO.outputs.forEach { sub ->
                if (outputRepo.findByMetricId(metricId).none { it.name.equals(sub.name, true) }) {
                    outputRepo.save(
                        Output(
                            id = null,
                            name = sub.name,
                            unit = sub.unit,
                            metricId = metricId
                        )
                    )
                }
            }

            // Metric parameters
            metricDTO.metricParameters.forEach { param ->
                if (parameterRepo.findByMetricId(metricId).none { it.name.equals(param.name, true) }) {
                    parameterRepo.save(
                        MetricParameter(
                            id = null,
                            name = param.name,
                            type = param.type,
                            metricId = metricId
                        )
                    )
                }
            }

            // Execution types and their parameters
            metricDTO.executionTypes.forEach { exec ->
                val execTypeId = executionTypeRepo.findAll()
                    .find { it.name.equals(exec.name, true) }?.id
                    ?: executionTypeRepo.save(
                        ExecutionType(id = null, name = exec.name, description = exec.description)
                    )

                // Link execution type to metric
                executionTypeMetricRepo.link(metricId, execTypeId)

                exec.parameters.forEach { param ->
                    val alreadyExists = executionTypeParamRepo
                        .getAllByExecutionType(execTypeId)
                        .any { it.name.equals(param.name, true) }

                    if (!alreadyExists) {
                        executionTypeParamRepo.save(
                            ExecutionTypeParameter(
                                id = null,
                                name = param.name,
                                type = param.type,
                                executionTypeId = execTypeId
                            )
                        )
                    }
                }
            }
        }

        println("✅ Métricas carregadas com sucesso do config.")
    }

    // DTO converters
    private fun Metric.toDTO() =
        MetricResponseDTO(id = this.id!!, name = name, description = description)

    private fun Output.toDTO() =
        OutputResponseDTO(id = this.id!!, metricId = metricId, name = name, unit = unit)

    private fun ExecutionType.toDTO() =
        ExecutionTypeResponseDTO(id = this.id!!, name = name, description = description)

    private fun MetricParameter.toDTO() =
        MetricParameterResponseDTO(id = this.id!!, metricId = metricId, name = name, type = type)
}
