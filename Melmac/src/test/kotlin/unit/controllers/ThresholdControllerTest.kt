package controllers

import controllers.IControllers.IThresholdController
import dtos.TestThresholdRequestDTO
import dtos.TestThresholdResponseDTO
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
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
import services.IServices.IThresholdService

class ThresholdControllerTest : KoinTest {

    companion object {
        private val service = mockk<IThresholdService>()
        val mockModule = module {
            single<IThresholdService> { service }
            single<IThresholdController> { ThresholdController(get()) }
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
    fun `GET thresholds by testPlanVersionId returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(TestThresholdResponseDTO(1, 100, 1, 2, 3))
        coEvery { service.getThresholdByTestPlanVersionId(1) } returns expected

        val response = client.get("/thresholds?testPlanVersionId=1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET thresholds missing testPlanVersionId returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/thresholds") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET threshold by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdController>()
                with(controller) { routes() }
            }
        }

        val expected = TestThresholdResponseDTO(1, 100, 1, 2, 3)
        coEvery { service.getThresholdById(1) } returns expected

        val response = client.get("/thresholds/1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET threshold by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/thresholds/abc") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET threshold by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getThresholdById(99) } returns null

        val response = client.get("/thresholds/99") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST threshold creates and returns created`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdController>()
                with(controller) { routes() }
            }
        }

        val request = TestThresholdRequestDTO(100, "1", 1, 2)
        val created = TestThresholdResponseDTO(10, 100, 1, 2, 3)
        coEvery { service.createTestThreshold(request) } returns created

        val response = client.post("/thresholds") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            contentType(ContentType.Application.Json)
            setBody(objectMapper.writeValueAsString(request))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val expectedJson = objectMapper.writeValueAsString(created)
        assertEquals(expectedJson, response.bodyAsText())
    }
}