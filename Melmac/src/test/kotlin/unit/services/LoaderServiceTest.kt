import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dtos.DataConfig
import domain.*
import io.mockk.*
import dtos.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*
import services.LoaderService
import java.time.Instant

class LoaderServiceTest {

    private val metricRepo = mockk<IMetricRepository>(relaxed = true)
    private val metricParameterRepo = mockk<IMetricParameterRepository>(relaxed = true)
    private val executionTypeRepo = mockk<IExecutionTypeRepository>(relaxed = true)
    private val executionTypeParameterRepo = mockk<IExecutionTypeParameterRepository>(relaxed = true)
    private val metricOutputRepo = mockk<IMetricOutputRepository>(relaxed = true)
    private val executionTypeMetricRepo = mockk<IExecutionTypeMetricRepository>(relaxed = true)
    private val thresholdTypeRepo = mockk<IThresholdTypeRepository>(relaxed = true)
    private val bootstrapUpdateRepo = mockk<IBootstrapUpdateRepository>(relaxed = true)

    private fun loaderWithConfig(config: DataConfig): LoaderService {
        val loader = LoaderService(
            metricRepo,
            metricParameterRepo,
            executionTypeRepo,
            executionTypeParameterRepo,
            metricOutputRepo,
            executionTypeMetricRepo,
            thresholdTypeRepo,
            bootstrapUpdateRepo,
            "data.json"
        )
        mockkObject(loader)
        every { loader.loadMetricsConfig() } returns config
        return loader
    }

    @Test
    fun `syncDataFromConfig does nothing if file is not newer than db`() {
        val now = Instant.now()
        val config = DataConfig(
            lastUpdated = now.toString(),
            thresholdTypes = emptyList(),
            metrics = emptyList()
        )
        every { bootstrapUpdateRepo.getLatestUpdateDate() } returns now

        val loader = loaderWithConfig(config)
        loader.syncDataFromConfig()

        verify { bootstrapUpdateRepo.getLatestUpdateDate() }
        verify(exactly = 0) { bootstrapUpdateRepo.save(any()) }
    }

    @Test
    fun `syncDataFromConfig saves new threshold types and metrics`() {
        val now = Instant.now()
        val config = DataConfig(
            lastUpdated = now.plusSeconds(10).toString(),
            thresholdTypes = listOf(
                ThresholdTypeConfigDTO("MAX", "desc")
            ),
            metrics = listOf(
                dtos.MetricConfigDTO(
                    name = "metric1",
                    metricParameters = listOf(
                        dtos.MetricParameterConfigDTO("param1", "type1")
                    ),
                    outputs = listOf(
                        dtos.MetricOutputConfigDTO("out1", "unit1")
                    ),
                    executionTypes = listOf(
                        dtos.ExecutionTypeConfigDTO(
                            executionTypeName = "execType1",
                            executionTypeDescription = "desc",
                            parameters = listOf(
                                dtos.ExecutionTypeParameterConfigDTO("etParam1", "etType1")
                            )
                        )
                    )
                )
            )
        )
        every { bootstrapUpdateRepo.getLatestUpdateDate() } returns now
        every { thresholdTypeRepo.findByName("MAX") } returns null
        every { metricRepo.findByName("metric1") } returns null
        every { metricRepo.save(any()) } returns 1
        every { metricParameterRepo.findByMetricIdAndName(1, "param1") } returns null
        every { metricOutputRepo.findByMetricIdAndName(1, "out1") } returns null
        every { executionTypeRepo.findByName("execType1") } returns null
        every { executionTypeRepo.save(any()) } returns 2
        every { executionTypeMetricRepo.link(1, 2) } just Runs
        every { executionTypeParameterRepo.findByExecutionTypeIdAndName(2, "etParam1") } returns null

        val loader = loaderWithConfig(config)
        loader.syncDataFromConfig()

        verify { thresholdTypeRepo.save(match { it.thresholdTypeName == "MAX" }) }
        verify { metricRepo.save(match { it.metricName == "metric1" }) }
        verify { metricParameterRepo.save(match { it.parameterName == "param1" }) }
        verify { metricOutputRepo.save(match { it.outputName == "out1" }) }
        verify { executionTypeRepo.save(match { it.executionTypeName == "execType1" }) }
        verify { executionTypeMetricRepo.link(1, 2) }
        verify { executionTypeParameterRepo.save(match { it.parameterName == "etParam1" }) }
    }

    @Test
    fun `syncDataFromConfig updates threshold type description if changed`() {
        val now = Instant.now()
        val config = DataConfig(
            lastUpdated = now.plusSeconds(10).toString(),
            thresholdTypes = listOf(
                ThresholdTypeConfigDTO("MAX", "newdesc")
            ),
            metrics = emptyList()
        )
        val existingType = ThresholdType(1, "MAX", "olddesc")
        every { bootstrapUpdateRepo.getLatestUpdateDate() } returns now
        every { thresholdTypeRepo.findByName("MAX") } returns existingType

        val loader = loaderWithConfig(config)
        loader.syncDataFromConfig()

        verify { thresholdTypeRepo.update(match { it.thresholdTypeDescription == "newdesc" }) }
    }
}