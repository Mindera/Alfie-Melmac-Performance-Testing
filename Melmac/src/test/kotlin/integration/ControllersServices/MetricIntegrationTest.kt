import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.MetricController
import controllers.IControllers.IMetricController
import domain.*
import dtos.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import repos.IRepos.*
import services.MetricService
import services.IServices.IMetricService

class MetricIntegrationTest : KoinTest {

    companion object {
        private val metricRepo = mockk<IMetricRepository>()
        private val metricOutputRepo = mockk<IMetricOutputRepository>()
        private val metricParameterRepo = mockk<IMetricParameterRepository>()
        private val executionTypeRepo = mockk<IExecutionTypeRepository>()
        private val executionTypeParameterRepo = mockk<IExecutionTypeParameterRepository>()
        private val metricMapper = mockk<MetricMapper>()
        private val metricOutputMapper = mockk<MetricOutputMapper>()
        private val metricParameterMapper = mockk<MetricParameterMapper>()
        private val executionTypeMapper = mockk<ExecutionTypeMapper>()
        private val executionTypeParameterMapper = mockk<ExecutionTypeParameterMapper>()

        // Use spyk if you want to partially mock, but here plain instance is fine
        private val service = MetricService(
            metricRepo,
            metricOutputRepo,
            metricParameterRepo,
            executionTypeRepo,
            executionTypeParameterRepo,
            metricMapper,
            metricOutputMapper,
            metricParameterMapper,
            executionTypeMapper,
            executionTypeParameterMapper
        )

        val module = module {
            single<IMetricRepository> { metricRepo }
            single<IMetricOutputRepository> { metricOutputRepo }
            single<IMetricParameterRepository> { metricParameterRepo }
            single<IExecutionTypeRepository> { executionTypeRepo }
            single<IExecutionTypeParameterRepository> { executionTypeParameterRepo }
            single<MetricMapper> { metricMapper }
            single<MetricOutputMapper> { metricOutputMapper }
            single<MetricParameterMapper> { metricParameterMapper }
            single<ExecutionTypeMapper> { executionTypeMapper }
            single<ExecutionTypeParameterMapper> { executionTypeParameterMapper }
            single<IMetricService> { service }
            single<IMetricController> { MetricController(get()) }
        }

        @JvmField
        @RegisterExtension
        val koinExtension = KoinTestExtension.create { modules(module) }
    }

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private fun testRoutes(builder: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }
        builder()
    }

    @Test
    fun `GET all metrics returns list`() = testRoutes {
        val metric = Metric(1, "Throughput")
        val dto = MetricResponseDTO(1, "Throughput")
        every { metricRepo.findAll() } returns listOf(metric)
        every { metricMapper.toDto(metric) } returns dto

        val response = client.get("/metrics")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun `GET metric by ID - valid`() = testRoutes {
        val metric = Metric(1, "Throughput")
        val dto = MetricResponseDTO(1, "Throughput")
        every { metricRepo.findById(1) } returns metric
        every { metricMapper.toDto(metric) } returns dto

        val response = client.get("/metrics/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dto), response.bodyAsText())
    }

    @Test
    fun `GET metric by ID - invalid ID returns 400`() = testRoutes {
        val response = client.get("/metrics/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET metric by ID - not found returns 404`() = testRoutes {
        every { metricRepo.findById(99) } returns null

        val response = client.get("/metrics/99")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Metric not found.", response.bodyAsText())
    }

    @Test
    fun `GET metric parameters - valid`() = testRoutes {
        val param = MetricParameter(1, "window", "String", 1)
        val dto = MetricParameterResponseDTO(1, "window", "String", 1)
        every { metricParameterRepo.findByMetricId(1) } returns listOf(param)
        every { metricParameterMapper.toDto(param) } returns dto

        val response = client.get("/metrics/1/parameters")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun `GET metric parameters - invalid ID returns 400`() = testRoutes {
        val response = client.get("/metrics/abc/parameters")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET metric outputs - valid`() = testRoutes {
        val output = MetricOutput(1, "avg", "ms", 1)
        val dto = MetricOutputResponseDTO(1, "avg", "ms", 1)
        every { metricOutputRepo.findByMetricId(1) } returns listOf(output)
        every { metricOutputMapper.toDto(output) } returns dto

        val response = client.get("/metrics/1/outputs")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun `GET metric outputs - invalid ID returns 400`() = testRoutes {
        val response = client.get("/metrics/abc/outputs")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET metric execution types - valid`() = testRoutes {
        val execType = ExecutionType(1, "PERFORMANCE", "Performance execution type")
        val dto = ExecutionTypeResponseDTO(1, "PERFORMANCE", "Performance execution type")
        every { executionTypeRepo.findByMetricId(1) } returns listOf(execType)
        every { executionTypeMapper.toDto(execType) } returns dto

        val response = client.get("/metrics/1/execution-types")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun `GET metric execution types - invalid ID returns 400`() = testRoutes {
        val response = client.get("/metrics/abc/execution-types")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET execution type parameters - valid`() = testRoutes {
        val param = ExecutionTypeParameter(1, "duration", "String", 1)
        val dto = ExecutionTypeParameterResponseDTO(1, "duration", "String", 1)
        every { executionTypeParameterRepo.findByExecutionTypeId(1) } returns listOf(param)
        every { executionTypeParameterMapper.toDto(param) } returns dto

        val response = client.get("/execution-types/1/parameters")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun `GET execution type parameters - invalid ID returns 400`() = testRoutes {
        val response = client.get("/execution-types/abc/parameters")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid execution type ID.", response.bodyAsText())
    }
}