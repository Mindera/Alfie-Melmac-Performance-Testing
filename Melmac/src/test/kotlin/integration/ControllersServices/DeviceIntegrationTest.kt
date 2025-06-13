package integration

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.DeviceController
import controllers.IControllers.IDeviceController
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
import io.mockk.spyk
import mappers.AvailableDeviceMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import repos.IRepos.*
import services.DeviceService
import services.IServices.IDeviceService

class DeviceIntegrationTest : KoinTest {

    companion object {
        private val deviceRepo = mockk<IDeviceRepository>()
        private val osVersionRepo = mockk<IOperSysVersionRepository>()
        private val osRepo = mockk<IOperSysRepository>()
        private val deviceMapper = mockk<AvailableDeviceMapper>()

        private val service = spyk(DeviceService(deviceRepo, osVersionRepo, osRepo, deviceMapper))
        
        val module = module {
            single<IDeviceRepository> { deviceRepo }
            single<IOperSysVersionRepository> { osVersionRepo }
            single<IOperSysRepository> { osRepo }
            single<AvailableDeviceMapper> { deviceMapper }
            single<IDeviceService> { service }
            single<IDeviceController> { DeviceController(get()) }
        }

        @JvmField
        @RegisterExtension
        val koinExtension = KoinTestExtension.create { modules(module) }
    }

    private val objectMapper =
            jacksonObjectMapper().apply {
                registerModule(JavaTimeModule())
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }

    private fun testRoutes(builder: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }
        builder()
    }

    @Test
    fun getDeviceByIdReturnsDTO() = testRoutes {
        val device = Device(1, "devName", "serial", 2)
        val osVersion = OSVersion(2, "14.0", 3)
        val os = OperativeSystem(3, "iOS")
        val dto = AvailableDeviceDTO(1, "devName", "serial", "iOS", "14.0")

        every { deviceRepo.findById(1) } returns device
        every { osVersionRepo.findById(2) } returns osVersion
        every { osRepo.findById(3) } returns os
        every { deviceMapper.toDto(device, "iOS", "14.0") } returns dto

        val response = client.get("/devices/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dto), response.bodyAsText())
    }

    @Test
    fun getDeviceByIdReturns404IfNotFound() = testRoutes {
        every { deviceRepo.findById(1) } returns null

        val response = client.get("/devices/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Device not found", response.bodyAsText())
    }

    @Test
    fun getAllDevicesReturnsList() = testRoutes {
        val dto1 = AvailableDeviceDTO(1, "dev1", "serial1", "iOS", "14.0")
        val dto2 = AvailableDeviceDTO(2, "dev2", "serial2", "Android", "30")

        every { service["fetchAllDevices"]() } returns listOf(dto1, dto2)

        val response = client.get("/devices")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto1, dto2)), response.bodyAsText())
    }

    @Test
    fun getAllDevicesFilteredByPlatform() = testRoutes {
        val dto1 = AvailableDeviceDTO(1, "dev1", "serial1", "android", "12")
        val dto2 = AvailableDeviceDTO(2, "dev2", "serial2", "ios", "15")
        every { service["fetchAllDevices"]() } returns listOf(dto1, dto2)

        val response = client.get("/devices?platform=android")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto1)), response.bodyAsText())
    }

    @Test
    fun getDeviceByIdReturns400IfInvalidId() = testRoutes {
        val response = client.get("/devices/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid Device ID", response.bodyAsText())
    }

    @Test
    fun getDevicesByMinOsVersion() = testRoutes {
        val dto1 = AvailableDeviceDTO(1, "dev1", "serial1", "android", "12")
        val dto2 = AvailableDeviceDTO(2, "dev2", "serial2", "ios", "15")
        every { service["getAvailableDevicesByMinVersion"]("12") } returns listOf(dto1, dto2)

        val response = client.get("/devices/os-minimum/12")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto1, dto2)), response.bodyAsText())
    }

    @Test
    fun getDevicesByMinOsVersionFilteredByPlatform() = testRoutes {
        val dto1 = AvailableDeviceDTO(1, "dev1", "serial1", "android", "12")
        val dto2 = AvailableDeviceDTO(2, "dev2", "serial2", "ios", "15")
        every { service["getAvailableDevicesByMinVersion"]("12") } returns listOf(dto1, dto2)

        val response = client.get("/devices/os-minimum/12?platform=android")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(listOf(dto1)), response.bodyAsText())
    }
}
