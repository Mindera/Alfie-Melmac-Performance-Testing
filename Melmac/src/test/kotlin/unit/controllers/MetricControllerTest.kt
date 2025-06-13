package controllers

import controllers.IControllers.IMetricController
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import services.IServices.IMetricService
import dtos.ExecutionTypeResponseDTO
import dtos.MetricResponseDTO
import dtos.MetricParameterResponseDTO
import dtos.MetricOutputResponseDTO
import dtos.ExecutionTypeParameterResponseDTO

class MetricControllerTest : KoinTest {

    companion object {
        private val service = mockk<IMetricService>()
        val mockModule = module {
            single<IMetricService> { service }
            single<IMetricController> { MetricController(get()) }
        }

        @JvmField
        @RegisterExtension
        val koinTestExtension = KoinTestExtension.create { modules(mockModule) }
    }

    private val objectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun `GET all metrics returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(MetricResponseDTO(metricId = 1, metricName = "Throughput"))
        every { service.getAllMetrics() } returns expected

        val response = client.get("/metrics") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET metric by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val expected = MetricResponseDTO(metricId = 1, metricName = "Throughput")
        every { service.getMetricById(1) } returns expected

        val response = client.get("/metrics/1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET metric by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/metrics/abc") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET metric by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        every { service.getMetricById(99) } returns null

        val response = client.get("/metrics/99") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Metric not found.", response.bodyAsText())
    }

    @Test
    fun `GET metric parameters - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            MetricParameterResponseDTO(
                metricParameterId = 1,
                parameterName = "window",
                parameterType = "String",
                metricMetricId = 1
            )
        )
        every { service.getParametersByMetricId(1) } returns expected

        val response = client.get("/metrics/1/parameters") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET metric parameters - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/metrics/abc/parameters") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET metric outputs - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            MetricOutputResponseDTO(
                metricOutputId = 1,
                outputName = "avg",
                unit = "ms",
                metricMetricId = 1
            )
        )
        every { service.getOutputsByMetricId(1) } returns expected

        val response = client.get("/metrics/1/outputs") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET metric outputs - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/metrics/abc/outputs") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET metric execution types - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            ExecutionTypeResponseDTO(
                executionTypeId = 1,
                executionTypeName = "PERFORMANCE",
                executionTypeDescription = "Performance execution type"
            )
        )
        every { service.getExecutionTypesByMetricId(1) } returns expected

        val response = client.get("/metrics/1/execution-types") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET metric execution types - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/metrics/abc/execution-types") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid metric ID.", response.bodyAsText())
    }

    @Test
    fun `GET execution type parameters - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            ExecutionTypeParameterResponseDTO(
                executionTypeParameterId = 1,
                parameterName = "duration",
                parameterType = "String",
                executionTypeExecutionTypeId = 1
            )
        )
        every { service.getParametersByExecutionTypeId(1) } returns expected

        val response = client.get("/execution-types/1/parameters") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET execution type parameters - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IMetricController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/execution-types/abc/parameters") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid execution type ID.", response.bodyAsText())
    }
}