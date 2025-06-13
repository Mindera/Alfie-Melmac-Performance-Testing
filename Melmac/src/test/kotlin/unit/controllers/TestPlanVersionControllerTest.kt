package controllers

import controllers.IControllers.ITestPlanVersionController
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
import services.IServices.ITestPlanVersionService

class TestPlanVersionControllerTest : KoinTest {

    companion object {
        private val service = mockk<ITestPlanVersionService>()
        val mockModule = module {
            single<ITestPlanVersionService> { service }
            single<ITestPlanVersionController> { TestPlanVersionController(get()) }
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
    fun `GET test plan version by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        val expected =
            TestPlanVersionResponseDTO(
                testPlanVersionId = 1,
                version = "v1",
                creationTimestamp = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
                notes = "Initial version",
                testPlanTestPlanId = 10,
                deviceDeviceId = 100,
                appVersionAppVersionId = 1000,
                appPackage = "com.example.app",
                mainActivity = "com.example.app.MainActivity",
                executionTypeExecutionTypeId = 5,
                thresholds = emptyList(),
                metricParameters = emptyList(),
                executionTypeParameters = emptyList(),
                testSuiteVersionId = 20
            )
        coEvery { service.getTestPlanVersionById(1) } returns expected

        val response =
            client.get("/test-plan-versions/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET test plan version by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        val response =
            client.get("/test-plan-versions/abc") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET test plan version by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getTestPlanVersionById(99) } returns null

        val response =
            client.get("/test-plan-versions/99") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET all versions by test plan - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        val expected =
            listOf(
                TestPlanVersionResponseDTO(
                    testPlanVersionId = 1,
                    version = "v1",
                    creationTimestamp = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
                    notes = "Initial version",
                    testPlanTestPlanId = 10,
                    deviceDeviceId = 100,
                    appVersionAppVersionId = 1000,
                    appPackage = "com.example.app",
                    mainActivity = "com.example.app.MainActivity",
                    executionTypeExecutionTypeId = 5,
                    thresholds = emptyList(),
                    metricParameters = emptyList(),
                    executionTypeParameters = emptyList(),
                    testSuiteVersionId = 20
                ),
                TestPlanVersionResponseDTO(
                    testPlanVersionId = 2,
                    version = "v2",
                    creationTimestamp = LocalDateTime.of(2023, 2, 1, 12, 0, 0),
                    notes = "Second version",
                    testPlanTestPlanId = 10,
                    deviceDeviceId = 100,
                    appVersionAppVersionId = 1001,
                    appPackage = "com.example.app",
                    mainActivity = "com.example.app.MainActivity",
                    executionTypeExecutionTypeId = 5,
                    thresholds = emptyList(),
                    metricParameters = emptyList(),
                    executionTypeParameters = emptyList(),
                    testSuiteVersionId = 21
                )
            )
        coEvery { service.getTestPlanVersionsByTestPlanId(1) } returns expected

        val response =
            client.get("/test-plan-versions/by-test-plan/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET all versions by test plan - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        val response =
            client.get("/test-plan-versions/by-test-plan/abc") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET all versions by test plan - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getTestPlanVersionsByTestPlanId(99) } returns emptyList()

        val response =
            client.get("/test-plan-versions/by-test-plan/99") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET latest version by test plan - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        val expected =
            TestPlanVersionResponseDTO(
                testPlanVersionId = 2,
                version = "v2",
                creationTimestamp = LocalDateTime.of(2023, 2, 1, 12, 0, 0),
                notes = "Second version",
                testPlanTestPlanId = 10,
                deviceDeviceId = 100,
                appVersionAppVersionId = 1001,
                appPackage = "com.example.app",
                mainActivity = "com.example.app.MainActivity",
                executionTypeExecutionTypeId = 5,
                thresholds = emptyList(),
                metricParameters = emptyList(),
                executionTypeParameters = emptyList(),
                testSuiteVersionId = 21
            )
        coEvery { service.getLatestTestPlanVersionByTestPlanId(1) } returns expected

        val response =
            client.get("/test-plan-versions/latest-by-test-plan/1") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET latest version by test plan - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        val response =
            client.get("/test-plan-versions/latest-by-test-plan/abc") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET latest version by test plan - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<ITestPlanVersionController>()
                with(controller) { routes() }
            }
        }

        coEvery { service.getLatestTestPlanVersionByTestPlanId(99) } returns null

        val response =
            client.get("/test-plan-versions/latest-by-test-plan/99") {
                header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}