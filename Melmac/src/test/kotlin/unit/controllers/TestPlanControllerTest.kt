package controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.IControllers.ITestPlanController
import dtos.TestPlanResponseDTO
import dtos.TestPlanVersionRequestDTO
import dtos.TestPlanVersionResponseDTO
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
import services.IServices.ITestPlanService

class TestPlanControllerTest : KoinTest {

    companion object {
        private val service = mockk<ITestPlanService>()
        val mockModule = module {
            single<ITestPlanService> { service }
            single<ITestPlanController> { TestPlanController(get()) }
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
    fun `GET test plan by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanController>()
                with(controller) { routes() }
            }
        }

        val expected =
                TestPlanResponseDTO(testPlanId = 1, testName = "My Plan", metricMetricId = 42)
        coEvery { service.getTestPlanById(1) } returns expected

        val response =
                client.get("/test-plans/1") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test plan by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanController>()
                with(controller) { routes() }
            }
        }

        val response =
                client.get("/test-plans/abc") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET test plan by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getTestPlanById(99) } returns null

        val response =
                client.get("/test-plans/99") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST test plan creates and returns created`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanController>()
                with(controller) { routes() }
            }
        }

        val request =
                TestPlanVersionRequestDTO(
                        notes = "Initial version",
                        testName = "My Plan",
                        metricName = "Throughput",
                        deviceName = "Pixel 5",
                        appName = "MyApp",
                        appVersion = "1.0.0",
                        appPackage = "com.example.myapp",
                        mainActivity = "com.example.myapp.MainActivity",
                        executionType = "PERFORMANCE",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 123
                )
        val created =
                TestPlanVersionResponseDTO(
                        testPlanVersionId = 1,
                        version = "1.0",
                        creationTimestamp = LocalDateTime.now(),
                        notes = "Initial version",
                        testPlanTestPlanId = 1,
                        deviceDeviceId = 10,
                        appVersionAppVersionId = 100,
                        appPackage = "com.example.myapp",
                        mainActivity = "com.example.myapp.MainActivity",
                        executionTypeExecutionTypeId = 5,
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 123
                )
        coEvery { service.createTestPlanWithVersion(request) } returns created

        val response =
                client.post("/test-plans") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    contentType(ContentType.Application.Json)
                    setBody(objectMapper.writeValueAsString(request))
                }
        assertEquals(HttpStatusCode.Created, response.status)
        val expectedJson = objectMapper.writeValueAsString(created)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `POST test plan creation fails with server error`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanController>()
                with(controller) { routes() }
            }
        }

        val request =
                TestPlanVersionRequestDTO(
                        notes = "Initial version",
                        testName = "My Plan",
                        metricName = "Throughput",
                        deviceName = "Pixel 5",
                        appName = "MyApp",
                        appVersion = "1.0.0",
                        appPackage = "com.example.myapp",
                        mainActivity = "com.example.myapp.MainActivity",
                        executionType = "PERFORMANCE",
                        thresholds = emptyList(),
                        metricParameters = emptyList(),
                        executionTypeParameters = emptyList(),
                        testSuiteVersionId = 123
                )
        coEvery { service.createTestPlanWithVersion(request) } throws
                RuntimeException("Something went wrong")

        val response =
                client.post("/test-plans") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    contentType(ContentType.Application.Json)
                    setBody(objectMapper.writeValueAsString(request))
                }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        val expectedError =
                mapOf("error" to "Failed to create test plan", "message" to "Something went wrong")
        val expectedJson = objectMapper.writeValueAsString(expectedError)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `POST test plan with invalid request body returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanController>()
                with(controller) { routes() }
            }
        }

        // Send invalid JSON (e.g., missing required fields)
        val invalidJson = """{ "invalid": "data" }"""

        val response =
                client.post("/test-plans") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                    contentType(ContentType.Application.Json)
                    setBody(invalidJson)
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid request body", response.bodyAsText())
    }

    @Test
    fun `POST test plan creation fails with IllegalArgumentException returns 400`() =
            testApplication {
                application {
                    install(ContentNegotiation) {
                        jackson {
                            registerModule(JavaTimeModule())
                            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        }
                    }
                    routing {
                        val controller = get<ITestPlanController>()
                        with(controller) { routes() }
                    }
                }

                val request =
                        TestPlanVersionRequestDTO(
                                notes = "Initial version",
                                testName = "My Plan",
                                metricName = "Throughput",
                                deviceName = "Pixel 5",
                                appName = "MyApp",
                                appVersion = "1.0.0",
                                appPackage = "com.example.myapp",
                                mainActivity = "com.example.myapp.MainActivity",
                                executionType = "PERFORMANCE",
                                thresholds = emptyList(),
                                metricParameters = emptyList(),
                                executionTypeParameters = emptyList(),
                                testSuiteVersionId = 123
                        )
                coEvery { service.createTestPlanWithVersion(request) } throws
                        IllegalArgumentException("Invalid data provided")

                val response =
                        client.post("/test-plans") {
                            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                            contentType(ContentType.Application.Json)
                            setBody(objectMapper.writeValueAsString(request))
                        }
                assertEquals(HttpStatusCode.BadRequest, response.status)
                assertEquals("Invalid data provided", response.bodyAsText())
            }
}
