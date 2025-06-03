package services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import domain.*
import dtos.DataConfig
import repos.IRepos.*
import services.IServices.ILoaderService
import java.io.File
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Service implementation for loading and synchronizing configuration data
 * from a JSON file into the application's repositories.
 *
 * @property metricRepo Repository for Metric entities.
 * @property metricParameterRepo Repository for MetricParameter entities.
 * @property executionTypeRepo Repository for ExecutionType entities.
 * @property executionTypeParameterRepo Repository for ExecutionTypeParameter entities.
 * @property metricOutputRepo Repository for MetricOutput entities.
 * @property executionTypeMetricRepo Repository for ExecutionTypeMetric join entities.
 * @property thresholdTypeRepo Repository for ThresholdType entities.
 * @property configFilePath Path to the configuration JSON file.
 */
class LoaderService(
    private val metricRepo: IMetricRepository,
    private val metricParameterRepo: IMetricParameterRepository,
    private val executionTypeRepo: IExecutionTypeRepository,
    private val executionTypeParameterRepo: IExecutionTypeParameterRepository,
    private val metricOutputRepo: IMetricOutputRepository,
    private val executionTypeMetricRepo: IExecutionTypeMetricRepository,
    private val thresholdTypeRepo: IThresholdTypeRepository,
    private val bootstrapUpdateRepo: IBootstrapUpdateRepository,
    private val configFilePath: String = "data.json"
) : ILoaderService {

    /**
     * Synchronizes data from the configuration file into the repositories.
     * Loads threshold types, metrics, metric parameters, outputs, execution types,
     * execution type parameters, and links execution types to metrics.
     */
    override fun syncDataFromConfig() {
        val config = loadMetricsConfig()

        val fileLastUpdated = try {
            Instant.parse(config.lastUpdated)
        } catch (e: DateTimeParseException) {
            throw IllegalStateException("Invalid lastUpdated format in data.json: ${config.lastUpdated}")
        }

        val dbLastUpdated = bootstrapUpdateRepo.getLatestUpdateDate()

        if (dbLastUpdated != null && !fileLastUpdated.isAfter(dbLastUpdated)) {
            return
        }

        if (dbLastUpdated == null || fileLastUpdated != dbLastUpdated) {
            bootstrapUpdateRepo.save(fileLastUpdated)
        }

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

    /**
     * Loads the metrics configuration from the JSON file.
     *
     * @return [DataConfig] object parsed from the configuration file.
     * @throws IllegalStateException if the configuration file is not found.
     */
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