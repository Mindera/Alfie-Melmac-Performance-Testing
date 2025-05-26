package services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import domain.*
import dtos.DataConfig
import repos.IRepos.*
import services.IServices.ILoaderService
import java.io.File

class LoaderService(
    private val metricRepo: IMetricRepository,
    private val metricParameterRepo: IMetricParameterRepository,
    private val executionTypeRepo: IExecutionTypeRepository,
    private val executionTypeParameterRepo: IExecutionTypeParameterRepository,
    private val metricOutputRepo: IMetricOutputRepository,
    private val executionTypeMetricRepo: IExecutionTypeMetricRepository,
    private val thresholdTypeRepo: IThresholdTypeRepository,
    private val configFilePath: String = "data.json"
) : ILoaderService {

    override fun syncDataFromConfig() {
        val config = loadMetricsConfig()

        // Load threshold types
        config.thresholdTypes.forEach { configThresholdType ->
            val existingType = thresholdTypeRepo.findByName(configThresholdType.thresholdTypeName)
            if (existingType == null) {
                thresholdTypeRepo.save(
                    ThresholdType(
                        thresholdTypeId = null,
                        thresholdTypeName = configThresholdType.thresholdTypeName,
                        thresholdTypeDescription = configThresholdType.thresholdTypeDescription
                    )
                )
            } else if (existingType.thresholdTypeDescription != configThresholdType.thresholdTypeDescription) {
                thresholdTypeRepo.update(
                    existingType.copy(thresholdTypeDescription = configThresholdType.thresholdTypeDescription)
                )
            }
        }

        // Load metrics and related entities
        config.metrics.forEach { configMetric ->
            val existingMetric = metricRepo.findByName(configMetric.name)
            val dbMetricId =
                if (existingMetric == null) {
                    metricRepo.save(Metric(null, configMetric.name))
                } else {
                    existingMetric.metricId!!
                }

            configMetric.metricParameters.forEach { param ->
                val existingParam =
                    metricParameterRepo.findByMetricIdAndName(dbMetricId, param.parameterName)
                if (existingParam == null) {
                    metricParameterRepo.save(
                        MetricParameter(
                            null,
                            param.parameterName,
                            param.parameterType,
                            dbMetricId
                        )
                    )
                } else if (existingParam.parameterType != param.parameterType) {
                    metricParameterRepo.update(
                        existingParam.copy(parameterType = param.parameterType)
                    )
                }
            }

            configMetric.outputs.forEach { output ->
                val existingOutput =
                    metricOutputRepo.findByMetricIdAndName(dbMetricId, output.outputName)
                if (existingOutput == null) {
                    metricOutputRepo.save(
                        MetricOutput(null, output.outputName, output.unit, dbMetricId)
                    )
                } else if (existingOutput.unit != output.unit) {
                    metricOutputRepo.update(existingOutput.copy(unit = output.unit))
                }
            }

            configMetric.executionTypes.forEach { execType ->
                val existingExecTypeByName = executionTypeRepo.findByName(execType.executionTypeName)
                val dbExecTypeId =
                    if (existingExecTypeByName == null) {
                        executionTypeRepo.save(
                            ExecutionType(
                                null,
                                execType.executionTypeName,
                                execType.executionTypeDescription
                            )
                        )
                    } else {
                        if (existingExecTypeByName.executionTypeDescription != execType.executionTypeDescription) {
                            executionTypeRepo.update(
                                existingExecTypeByName.copy(
                                    executionTypeDescription = execType.executionTypeDescription
                                )
                            )
                        }
                        existingExecTypeByName.executionTypeId!!
                    }

                // Ensure the ExecutionType is linked to the Metric in the join table
                executionTypeMetricRepo.link(dbMetricId, dbExecTypeId)

                execType.parameters.forEach { param ->
                    val existingParam =
                        executionTypeParameterRepo.findByExecutionTypeIdAndName(
                            dbExecTypeId,
                            param.executionTypeParameterName
                        )
                    if (existingParam == null) {
                        executionTypeParameterRepo.save(
                            ExecutionTypeParameter(
                                null,
                                param.executionTypeParameterName,
                                param.executionTypeParameterType,
                                dbExecTypeId
                            )
                        )
                    } else if (existingParam.parameterType != param.executionTypeParameterType) {
                        executionTypeParameterRepo.update(
                            existingParam.copy(parameterType = param.executionTypeParameterType)
                        )
                    }
                }
            }
        }
    }

    private fun loadMetricsConfig(): DataConfig {
        val devFile = File("src/main/resources/data.json")
        val inputStream =
            when {
                devFile.exists() -> devFile.inputStream()
                else -> javaClass.classLoader.getResourceAsStream("data.json")
                    ?: throw IllegalStateException(
                        "data.json not found in dev path or resources."
                    )
            }
        return jacksonObjectMapper().readValue(inputStream)
    }
}