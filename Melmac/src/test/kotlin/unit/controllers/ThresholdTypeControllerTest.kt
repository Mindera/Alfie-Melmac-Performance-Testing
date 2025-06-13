package controllers

import controllers.IControllers.IThresholdTypeController
import dtos.ThresholdTypeResponseDTO
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
import services.IServices.IThresholdTypeService

class ThresholdTypeControllerTest : KoinTest {

    companion object {
        private val service = mockk<IThresholdTypeService>()
        val mockModule = module {
            single<IThresholdTypeService> { service }
            single<IThresholdTypeController> { ThresholdTypeController(get()) }
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
    fun `GET all threshold types returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            ThresholdTypeResponseDTO(
                thresholdTypeId = 1,
                thresholdTypeName = "MAX",
                thresholdTypeDescription = "Maximum threshold"
            ),
            ThresholdTypeResponseDTO(
                thresholdTypeId = 2,
                thresholdTypeName = "MIN",
                thresholdTypeDescription = "Minimum threshold"
            )
        )
        coEvery { service.getAll() } returns expected

        val response = client.get("/threshold-types") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET threshold type by ID - valid ID`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        val expected = ThresholdTypeResponseDTO(
            thresholdTypeId = 1,
            thresholdTypeName = "MAX",
            thresholdTypeDescription = "Maximum threshold"
        )
        coEvery { service.getById(1) } returns expected

        val response = client.get("/threshold-types/1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET threshold type by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/threshold-types/abc") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET threshold type by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getById(99) } returns null

        val response = client.get("/threshold-types/99") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET threshold type by name - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        val expected = ThresholdTypeResponseDTO(
            thresholdTypeId = 1,
            thresholdTypeName = "MAX",
            thresholdTypeDescription = "Maximum threshold"
        )
        coEvery { service.getByName("MAX") } returns expected

        val response = client.get("/threshold-types/by-name/MAX") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET threshold type by name - blank returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/threshold-types/by-name/") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET threshold type by name - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IThresholdTypeController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getByName("UNKNOWN") } returns null

        val response = client.get("/threshold-types/by-name/UNKNOWN") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}