package controllers

import controllers.IControllers.ITestExecutionController
import dtos.TestExecutionResponseDTO
import dtos.TestMetricOutputResultResponseDTO
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
import services.IServices.ITestExecutionService
import services.IServices.ITestMetricOutputResultService
import java.time.LocalDateTime

class TestExecutionControllerTest : KoinTest {

    companion object {
        private val executionService = mockk<ITestExecutionService>()
        private val outputService = mockk<ITestMetricOutputResultService>()
        val mockModule = module {
            single<ITestExecutionService> { executionService }
            single<ITestMetricOutputResultService> { outputService }
            single<ITestExecutionController> { TestExecutionController(get(), get()) }
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
    fun `GET all test executions returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            TestExecutionResponseDTO(
                testExecutionId = 1,
                initialTimestamp = LocalDateTime.of(2024, 6, 1, 10, 0, 0),
                endTimestamp = LocalDateTime.of(2024, 6, 1, 10, 5, 0),
                passed = "YES",
                testPlanVersionTestPlanVersionId = 100
            )
        )
        coEvery { executionService.getAllTestExecutions() } returns expected

        val response = client.get("/test-executions") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test execution by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val expected = TestExecutionResponseDTO(
            testExecutionId = 1,
            initialTimestamp = LocalDateTime.of(2024, 6, 1, 10, 0, 0),
            endTimestamp = LocalDateTime.of(2024, 6, 1, 10, 5, 0),
            passed = "YES",
            testPlanVersionTestPlanVersionId = 100
        )

        coEvery { executionService.getTestExecutionById(1) } returns expected

        val response = client.get("/test-executions/1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test execution by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/test-executions/abc") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET test execution by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        coEvery { executionService.getTestExecutionById(99) } returns null

        val response = client.get("/test-executions/99") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST test executions run returns result`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val expected = TestExecutionResponseDTO(
            testExecutionId = 1,
            initialTimestamp = LocalDateTime.of(2024, 6, 1, 10, 0, 0),
            endTimestamp = LocalDateTime.of(2024, 6, 1, 10, 5, 0),
            passed = "YES",
            testPlanVersionTestPlanVersionId = 100
        )

        coEvery { executionService.runTestExecution(1) } returns expected

        val response = client.post("/test-executions/run?testPlanVersionId=1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `POST test executions run missing param returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val response = client.post("/test-executions/run") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET test executions outputs returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            TestMetricOutputResultResponseDTO(
                testMetricOutputResultId = 1,
                value = "42.0",
                metricOutputMetricOutputId = 10,
                testExecutionTestExecutionId = 1
            ),
            TestMetricOutputResultResponseDTO(
                testMetricOutputResultId = 2,
                value = "13.7",
                metricOutputMetricOutputId = 11,
                testExecutionTestExecutionId = 1
            )
        )
        coEvery { outputService.getByExecutionId(1) } returns expected

        val response = client.get("/test-executions/outputs?testExecutionId=1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test executions outputs missing param returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestExecutionController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/test-executions/outputs") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}