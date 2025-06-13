import domain.*
import dtos.*
import io.mockk.*
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*
import services.MetricService

class MetricServiceTest {

    private val metricRepository = mockk<IMetricRepository>()
    private val metricOutputRepository = mockk<IMetricOutputRepository>()
    private val metricParameterRepository = mockk<IMetricParameterRepository>()
    private val executionTypeRepository = mockk<IExecutionTypeRepository>()
    private val executionTypeParameterRepository = mockk<IExecutionTypeParameterRepository>()
    private val metricMapper = mockk<MetricMapper>()
    private val metricOutputMapper = mockk<MetricOutputMapper>()
    private val metricParameterMapper = mockk<MetricParameterMapper>()
    private val executionTypeMapper = mockk<ExecutionTypeMapper>()
    private val executionTypeParameterMapper = mockk<ExecutionTypeParameterMapper>()

    private val service = MetricService(
        metricRepository,
        metricOutputRepository,
        metricParameterRepository,
        executionTypeRepository,
        executionTypeParameterRepository,
        metricMapper,
        metricOutputMapper,
        metricParameterMapper,
        executionTypeMapper,
        executionTypeParameterMapper
    )

    @Test
    fun `getAllMetrics returns mapped DTOs`() {
        val metric = Metric(1, "metric")
        val dto = MetricResponseDTO(1, "metric")
        every { metricRepository.findAll() } returns listOf(metric)
        every { metricMapper.toDto(metric) } returns dto

        val result = service.getAllMetrics()

        assertEquals(listOf(dto), result)
        verify { metricRepository.findAll() }
        verify { metricMapper.toDto(metric) }
    }

    @Test
    fun `getMetricById returns mapped DTO when found`() {
        val metric = Metric(2, "metric2")
        val dto = MetricResponseDTO(2, "metric2")
        every { metricRepository.findById(2) } returns metric
        every { metricMapper.toDto(metric) } returns dto

        val result = service.getMetricById(2)

        assertEquals(dto, result)
        verify { metricRepository.findById(2) }
        verify { metricMapper.toDto(metric) }
    }

    @Test
    fun `getMetricById returns null when not found`() {
        every { metricRepository.findById(99) } returns null

        val result = service.getMetricById(99)

        assertNull(result)
        verify { metricRepository.findById(99) }
    }

    @Test
    fun `getOutputsByMetricId returns mapped DTOs`() {
        val output = MetricOutput(1, "output", "unit", 2)
        val dto = MetricOutputResponseDTO(1, "output", "unit", 2)
        every { metricOutputRepository.findByMetricId(2) } returns listOf(output)
        every { metricOutputMapper.toDto(output) } returns dto

        val result = service.getOutputsByMetricId(2)

        assertEquals(listOf(dto), result)
        verify { metricOutputRepository.findByMetricId(2) }
        verify { metricOutputMapper.toDto(output) }
    }

    @Test
    fun `getParametersByMetricId returns mapped DTOs`() {
        val param = MetricParameter(1, "param", "desc", 2)
        val dto = MetricParameterResponseDTO(1, "param", "desc", 2)
        every { metricParameterRepository.findByMetricId(2) } returns listOf(param)
        every { metricParameterMapper.toDto(param) } returns dto

        val result = service.getParametersByMetricId(2)

        assertEquals(listOf(dto), result)
        verify { metricParameterRepository.findByMetricId(2) }
        verify { metricParameterMapper.toDto(param) }
    }

    @Test
    fun `getExecutionTypesByMetricId returns mapped DTOs`() {
        val execType = ExecutionType(1, "type", "desc")
        val dto = ExecutionTypeResponseDTO(1, "type", "desc")
        every { executionTypeRepository.findByMetricId(2) } returns listOf(execType)
        every { executionTypeMapper.toDto(execType) } returns dto

        val result = service.getExecutionTypesByMetricId(2)

        assertEquals(listOf(dto), result)
        verify { executionTypeRepository.findByMetricId(2) }
        verify { executionTypeMapper.toDto(execType) }
    }

    @Test
    fun `getParametersByExecutionTypeId returns mapped DTOs`() {
        val param = ExecutionTypeParameter(1, "param", "desc", 2)
        val dto = ExecutionTypeParameterResponseDTO(1, "param", "desc", 2)
        every { executionTypeParameterRepository.findByExecutionTypeId(2) } returns listOf(param)
        every { executionTypeParameterMapper.toDto(param) } returns dto

        val result = service.getParametersByExecutionTypeId(2)

        assertEquals(listOf(dto), result)
        verify { executionTypeParameterRepository.findByExecutionTypeId(2) }
        verify { executionTypeParameterMapper.toDto(param) }
    }
}