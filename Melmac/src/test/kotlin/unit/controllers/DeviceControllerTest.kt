package controllers

import controllers.IControllers.IDeviceController
import dtos.AvailableDeviceDTO
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
import services.IServices.IDeviceService

class DeviceControllerTest : KoinTest {

    companion object {
        private val service = mockk<IDeviceService>()
        val mockModule = module {
            single<IDeviceService> { service }
            single<IDeviceController> { DeviceController(get()) }
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
    fun `GET all devices returns list`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            AvailableDeviceDTO(1, "Pixel 5", osName = "android", osVersion = "12"),
            AvailableDeviceDTO(2, "iPhone 13", osName = "ios", osVersion = "15")
        )
        every { service.getAllAvailableDevices() } returns expected

        val response = client.get("/devices") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET all devices filtered by platform`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        val allDevices = listOf(
            AvailableDeviceDTO(1, "Pixel 5", osName = "android", osVersion = "12"),
            AvailableDeviceDTO(2, "iPhone 13", osName = "ios", osVersion = "15")
        )
        every { service.getAllAvailableDevices() } returns allDevices

        val response = client.get("/devices?platform=android") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(listOf(allDevices[0]))
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET device by ID - valid`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        val expected = AvailableDeviceDTO(1, "Pixel 5", osName = "android", osVersion = "12")
        every { service.getDeviceById(1) } returns expected

        val response = client.get("/devices/1") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET device by ID - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        val response = client.get("/devices/abc") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid Device ID", response.bodyAsText())
    }

    @Test
    fun `GET device by ID - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        every { service.getDeviceById(99) } returns null

        val response = client.get("/devices/99") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Device not found", response.bodyAsText())
    }

    @Test
    fun `GET devices by min OS version`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        val expected = listOf(
            AvailableDeviceDTO(1, "Pixel 5", "android", "12", "12")
        )
        every { service.getAvailableDevicesByMinVersion("12") } returns expected

        val response = client.get("/devices/os-minimum/12") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(expected)
        assertEquals(expectedJson, response.bodyAsText())
    }

    @Test
    fun `GET devices by min OS version filtered by platform`() = testApplication {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(JavaTimeModule())
                    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                }
            }
            routing {
                val controller = get<IDeviceController>()
                with(controller) { routes() }
            }
        }

        val allDevices = listOf(
            AvailableDeviceDTO(1, "Pixel 5", osName = "android", osVersion = "12"),
            AvailableDeviceDTO(2, "iPhone 13", osName = "ios", osVersion = "15")
        )
        every { service.getAvailableDevicesByMinVersion("12") } returns allDevices

        val response = client.get("/devices/os-minimum/12?platform=android") {
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val expectedJson = objectMapper.writeValueAsString(listOf(allDevices[0]))
        assertEquals(expectedJson, response.bodyAsText())
    }
}