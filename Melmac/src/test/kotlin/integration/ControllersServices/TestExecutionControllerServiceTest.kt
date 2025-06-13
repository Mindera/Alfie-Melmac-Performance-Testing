package integration

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.IControllers.ITestExecutionController
import controllers.TestExecutionController
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
import io.mockk.*
import mappers.TestExecutionMapper
import mappers.TestMetricOutputResultMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import repos.IRepos.*
import services.*
import services.IServices.ITestExecutionService
import services.IServices.ITestMetricOutputResultService

import java.time.LocalDateTime

class TestExecutionIntegrationTest : KoinTest {

    companion object {
        // Repos
        private val testExecutionRepository = mockk<ITestExecutionRepository>()
        private val testPlanVersionRepository = mockk<ITestPlanVersionRepository>()
        private val metricRepository = mockk<IMetricRepository>()
        private val metricOutputRepository = mockk<IMetricOutputRepository>()
        private val executionTypeRepository = mockk<IExecutionTypeRepository>()
        private val deviceRepository = mockk<IDeviceRepository>()
        private val osVersionRepository = mockk<IOperSysVersionRepository>()
        private val osRepository = mockk<IOperSysRepository>()
        private val appVersionRepository = mockk<IAppVersionRepository>()
        private val appRepository = mockk<IAppRepository>()
        private val testPlanExecutionTypeParamValueRepo = mockk<ITestPlanExecutionTypeParameterValueRepository>()
        private val testPlanMetricParamValueRepo = mockk<ITestPlanMetricParameterValueRepository>()
        private val testPlanRepository = mockk<ITestPlanRepository>()
        private val metricParameterRepository = mockk<IMetricParameterRepository>()
        private val executionTypeParameterRepository = mockk<IExecutionTypeParameterRepository>()
        private val testThresholdRepository = mockk<IThresholdRepository>()
        private val thresholdTypeRepository = mockk<IThresholdTypeRepository>()
        private val testMetricOutputResultRepository = mockk<ITestMetricOutputResultRepository>()
        private val testRunner = mockk<core.runners.ITestRunner>()

        // Mappers
        private val executionMapper = mockk<TestExecutionMapper>()
        private val resultMapper = mockk<TestMetricOutputResultMapper>()

        // Services
        private val executionService = spyk(
            TestExecutionService(
                testExecutionRepository,
                testPlanVersionRepository,
                metricRepository,
                metricOutputRepository,
                executionTypeRepository,
                deviceRepository,
                osVersionRepository,
                osRepository,
                appVersionRepository,
                appRepository,
                testPlanExecutionTypeParamValueRepo,
                testPlanMetricParamValueRepo,
                testPlanRepository,
                metricParameterRepository,
                executionTypeParameterRepository,
                testThresholdRepository,
                thresholdTypeRepository,
                testMetricOutputResultRepository,
                testRunner,
                executionMapper,
                resultMapper
            )
        )
        private val resultService = spyk(TestMetricOutputResultService(testMetricOutputResultRepository, resultMapper))

        val module = module {
            single<ITestExecutionRepository> { testExecutionRepository }
            single<ITestPlanVersionRepository> { testPlanVersionRepository }
            single<IMetricRepository> { metricRepository }
            single<IMetricOutputRepository> { metricOutputRepository }
            single<IExecutionTypeRepository> { executionTypeRepository }
            single<IDeviceRepository> { deviceRepository }
            single<IOperSysVersionRepository> { osVersionRepository }
            single<IOperSysRepository> { osRepository }
            single<IAppVersionRepository> { appVersionRepository }
            single<IAppRepository> { appRepository }
            single<ITestPlanExecutionTypeParameterValueRepository> { testPlanExecutionTypeParamValueRepo }
            single<ITestPlanMetricParameterValueRepository> { testPlanMetricParamValueRepo }
            single<ITestPlanRepository> { testPlanRepository }
            single<IMetricParameterRepository> { metricParameterRepository }
            single<IExecutionTypeParameterRepository> { executionTypeParameterRepository }
            single<IThresholdRepository> { testThresholdRepository }
            single<IThresholdTypeRepository> { thresholdTypeRepository }
            single<ITestMetricOutputResultRepository> { testMetricOutputResultRepository }
            single<core.runners.ITestRunner> { testRunner }

            single<TestExecutionMapper> { executionMapper }
            single<TestMetricOutputResultMapper> { resultMapper }

            single<ITestExecutionService> { executionService }
            single<ITestMetricOutputResultService> { resultService }

            single<ITestExecutionController> { TestExecutionController(get(), get()) }
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
        builder()
    }

    @Test
    fun getAllTestExecutionsReturnsList() = testRoutes {
        val now = LocalDateTime.now()
        val entity = TestExecution(1, now, now, "YES", 100)
        val dto = TestExecutionResponseDTO(1, now, now, "YES", 100)

        every { testExecutionRepository.findAll() } returns listOf(entity)
        every { executionMapper.toDto(entity) } returns dto

        val response = client.get("/test-executions")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun getTestExecutionByIdReturnsDTO() = testRoutes {
        val now = LocalDateTime.now()
        val entity = TestExecution(1, now, now, "YES", 100)
        val dto = TestExecutionResponseDTO(1, now, now, "YES", 100)

        every { testExecutionRepository.findById(1) } returns entity
        every { executionMapper.toDto(entity) } returns dto

        val response = client.get("/test-executions/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dto), response.bodyAsText())
    }

    @Test
    fun getMetricResultsByExecutionIdReturnsList() = testRoutes {
        val entity = TestMetricOutputResult(1, "42.0", 3, 2)
        val dto = TestMetricOutputResultResponseDTO(1, "42.0", 3, 2)

        coEvery { testMetricOutputResultRepository.getByExecutionId(2) } returns listOf(entity)
        every { resultMapper.toDto(entity) } returns dto

        val response = client.get("/test-executions/outputs?testExecutionId=2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto)), response.bodyAsText())
    }

    @Test
    fun runTestExecutionReturnsResult() = testRoutes {
        val now = LocalDateTime.now()

        val testPlanVersion = TestPlanVersion(
            testPlanVersionId = 10,
            version = "v1",
            creationTimestamp = now,
            notes = "",
            appPackage = "pkg",
            mainActivity = "main",
            testPlanTestPlanId = 20,
            deviceDeviceId = 30,
            appVersionAppVersionId = 40,
            executionTypeExecutionTypeId = 50
        )
        val testPlan = TestPlan(20, "Plan", 60)
        val metric = Metric(60, "metric")
        val executionType = ExecutionType(50, "type", "")
        val device = Device(30, "dev", "sn", 70)
        val os = OperativeSystem(70, "Android")
        val appVersion = AppVersion(40, 80, "1.0")
        val app = App(80, "app")
        val execParam = TestExecutionTypeParameter(1, "v", 2, 10)
        val metricParam = TestMetricParameter(3, "v", 4, 10)
        val execTypeParam = ExecutionTypeParameter(2, "etp", "", 50)
        val metricParamDef = MetricParameter(4, "mp", "", 60)
        val output = MetricOutput(100, "out", "ms", 60)
        val threshold = TestThreshold(1, 100, 5, 10, 100)
        val thresholdType = ThresholdType(5, "MAX", "desc")

        val expectedDto = TestExecutionResponseDTO(123, now, now, "true", 10)

        every { testPlanVersionRepository.findById(10) } returns testPlanVersion
        every { testPlanRepository.findById(20) } returns testPlan
        every { metricRepository.findById(60) } returns metric
        every { executionTypeRepository.findById(50) } returns executionType
        every { deviceRepository.findById(30) } returns device
        every { osRepository.findById(70) } returns os
        every { appVersionRepository.findById(40) } returns appVersion
        every { appRepository.findById(80) } returns app
        every { testPlanExecutionTypeParamValueRepo.findByTestPlanVersionId(10) } returns listOf(execParam)
        every { testPlanMetricParamValueRepo.findByTestPlanVersionId(10) } returns listOf(metricParam)
        every { executionTypeParameterRepository.findById(2) } returns execTypeParam
        every { metricParameterRepository.findById(4) } returns metricParamDef
        every { metricOutputRepository.findByMetricId(60) } returns listOf(output)
        every { testThresholdRepository.findByTestPlanVersionId(10) } returns listOf(threshold)
        every { thresholdTypeRepository.findById(5) } returns thresholdType

        every {
            executionMapper.toConfigDto(
                any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any()
            )
        } returns TestExecutionConfigDTO(
            "type", "metric",
            mapOf("mp" to "v"),
            mapOf("etp" to "v"),
            listOf(Triple("100", "MAX", "out")),
            "dev", "sn", "Android", "app", "1.0", "pkg", "main"
        )

        every { testRunner.run(any()) } returns mapOf("success" to "true", "out" to "42")
        every { testExecutionRepository.save(any()) } returns 123
        every { testMetricOutputResultRepository.save(any()) } returns 1
        every { executionMapper.toDto(any()) } returns expectedDto

        val response = client.post("/test-executions/run?testPlanVersionId=10")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expectedDto), response.bodyAsText())
    }

    @Test
    fun runTestExecutionMissingParamReturns400() = testRoutes {
        val response = client.post("/test-executions/run")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Missing or invalid testPlanVersionId", response.bodyAsText())
    }

    @Test
    fun getTestExecutionByIdReturns400IfInvalidId() = testRoutes {
        val response = client.get("/test-executions/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid Test Execution ID", response.bodyAsText())
    }

    @Test
    fun getTestExecutionByIdReturns404IfNotFound() = testRoutes {
        every { testExecutionRepository.findById(999) } returns null

        val response = client.get("/test-executions/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Test Execution not found", response.bodyAsText())
    }

    @Test
    fun getMetricResultsByExecutionIdReturns400IfMissingParam() = testRoutes {
        val response = client.get("/test-executions/outputs")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Missing or invalid testExecutionId", response.bodyAsText())
    }

    @Test
    fun getMetricResultsByExecutionIdReturnsEmptyListIfNoResults() = testRoutes {
        coEvery { testMetricOutputResultRepository.getByExecutionId(999) } returns emptyList()

        val response = client.get("/test-executions/outputs?testExecutionId=999")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("[]", response.bodyAsText())
    }
}
