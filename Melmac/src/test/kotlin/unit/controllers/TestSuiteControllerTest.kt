package controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.IControllers.ITestSuiteController
import dtos.SuiteExecutionResponseDTO
import dtos.TestSuiteRequestDTO
import dtos.TestSuiteResponseDTO
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
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import services.IServices.ITestExecutionService
import services.IServices.ITestSuiteService

class TestSuiteControllerTest : KoinTest {

    companion object {
        private val suiteService = mockk<ITestSuiteService>()
        private val execService = mockk<ITestExecutionService>()
        val mockModule = module {
            single<ITestSuiteService> { suiteService }
            single<ITestExecutionService> { execService }
            single<ITestSuiteController> { TestSuiteController(get(), get()) }
        }

        @JvmField
        @RegisterExtension
        val koinTestExtension = KoinTestExtension.create { modules(mockModule) }
    }

    private val objectMapper =
            jacksonObjectMapper().apply {
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }

    @Test
    fun `GET all test suites returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        val expected =
                listOf(
                        TestSuiteResponseDTO(1, "Suite1", null, LocalDateTime.now(), true),
                        TestSuiteResponseDTO(2, "Suite2", null, LocalDateTime.now(), false)
                )
        coEvery { suiteService.getAllTestSuites() } returns expected

        val response =
                client.get("/test-suites") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test suite by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        val expected = TestSuiteResponseDTO(1, "Suite1", null, LocalDateTime.now(), true)
        coEvery { suiteService.getTestSuiteById(1) } returns expected

        val response =
                client.get("/test-suites/1") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test suite by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        val response =
                client.get("/test-suites/abc") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET test suite by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        coEvery { suiteService.getTestSuiteById(99) } returns null

        val response =
                client.get("/test-suites/99") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST test suite creates and returns created`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        val request = TestSuiteRequestDTO("Suite1", null)
        val created = TestSuiteResponseDTO(1, "Suite1", null, LocalDateTime.now(), true)
        coEvery { suiteService.createTestSuite(request) } returns created

        val response =
                client.post("/test-suites") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    contentType(ContentType.Application.Json)
                    setBody(objectMapper.writeValueAsString(request))
                }
        assertEquals(HttpStatusCode.Created, response.status)
        val expectedJson = objectMapper.writeValueAsString(created)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `POST test suite run executes and returns result`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        val suiteExecution =
                SuiteExecutionResponseDTO(
                        suiteExecutionId = 1,
                        initialTimestamp = LocalDateTime.now(),
                        endTimestamp = LocalDateTime.now().plusMinutes(1),
                        testSuiteVersionTestSuiteVersionId = 1,
                        executionResults = emptyList()
                )
        coEvery { suiteService.runTestSuiteExecution(1) } returns suiteExecution

        val response =
                client.post("/test-suites/1/run") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(suiteExecution)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `POST test suite run with invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        val response =
                client.post("/test-suites/abc/run") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid Test Suite ID", response.bodyAsText())
    }

    @Test
    fun `POST test suite run throws exception returns 500`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestSuiteController>()
                with(controller) { routes() }
            }
        }

        coEvery { suiteService.runTestSuiteExecution(1) } throws
                RuntimeException("Suite execution failed")

        val response =
                client.post("/test-suites/1/run") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Suite execution failed", response.bodyAsText())
    }
}
