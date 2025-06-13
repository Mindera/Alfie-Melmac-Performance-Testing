package controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.IControllers.IAppController
import dtos.AppResponseDTO
import dtos.AppVersionResponseDTO
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import services.IServices.IAppService

class AppControllerTest : KoinTest {

    companion object {
        private val service = mockk<IAppService>()
        val mockModule = module {
            single<IAppService> { service }
            single<IAppController> { AppController(get()) }
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
    fun `GET all apps from db returns list`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected = listOf(AppResponseDTO(1, "MyApp"))
        every { service.getAllAppsFromDatabase() } returns expected

        val response =
                client.get("/apps/db") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET app by ID from db - valid`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected = AppResponseDTO(1, "MyApp")
        every { service.getAppByIdFromDatabase(1) } returns expected

        val response =
                client.get("/apps/db/1") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET app by ID from db - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val response =
                client.get("/apps/db/abc") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid App ID", response.bodyAsText())
    }

    @Test
    fun `GET app by ID from db - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        every { service.getAppByIdFromDatabase(99) } throws
                IllegalArgumentException("App not found")

        val response =
                client.get("/apps/db/99") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("App not found", response.bodyAsText())
    }

    @Test
    fun `GET app versions by app ID from db - valid`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected =
                listOf(AppVersionResponseDTO(1, 1, "1.0"), AppVersionResponseDTO(2, 1, "2.0"))
        every { service.getAppVersionsByAppIdFromDatabase(1) } returns expected

        val response =
                client.get("/apps/db/1/versions") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET app versions by app ID from db - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val response =
                client.get("/apps/db/abc/versions") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid App ID", response.bodyAsText())
    }

    @Test
    fun `GET app versions by app ID from db - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        every { service.getAppVersionsByAppIdFromDatabase(99) } throws
                IllegalArgumentException("App not found")

        val response =
                client.get("/apps/db/99/versions") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("App not found", response.bodyAsText())
    }

    @Test
    fun `GET app by version ID from db - valid`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected = AppResponseDTO(1, "MyApp")
        every { service.getAppByVersionIdFromDatabase(1) } returns expected

        val response =
                client.get("/apps/db/appByVersionId/1") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET app by version ID from db - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val response =
                client.get("/apps/db/appByVersionId/abc") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid App Version ID", response.bodyAsText())
    }

    @Test
    fun `GET app by version ID from db - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        every { service.getAppByVersionIdFromDatabase(99) } throws
                IllegalArgumentException("App not found")

        val response =
                client.get("/apps/db/appByVersionId/99") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("App not found", response.bodyAsText())
    }

    @Test
    fun `GET app version by ID from db - valid`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected = AppVersionResponseDTO(1, 1, "1.0")
        every { service.getAppVersionByIdFromDatabase(1) } returns expected

        val response =
                client.get("/apps/db/version/1") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET app version by ID from db - invalid ID returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val response =
                client.get("/apps/db/version/abc") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid App Version ID", response.bodyAsText())
    }

    @Test
    fun `GET app version by ID from db - not found returns 404`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        every { service.getAppVersionByIdFromDatabase(99) } throws
                IllegalArgumentException("App Version not found")

        val response =
                client.get("/apps/db/version/99") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("App Version not found", response.bodyAsText())
    }

    @Test
    fun `GET all apps from folder returns list`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected = listOf(AppResponseDTO(1, "MyApp"))
        every { service.getAllAppsFromFolder() } returns expected

        val response =
                client.get("/apps/folder") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET all apps from folder - error returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        every { service.getAllAppsFromFolder() } throws IllegalArgumentException("Folder error")

        val response =
                client.get("/apps/folder") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Folder error", response.bodyAsText())
    }

    @Test
    fun `GET app versions from folder - valid`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        val expected =
                listOf(AppVersionResponseDTO(1, 1, "1.0"), AppVersionResponseDTO(2, 1, "2.0"))
        every { service.getAppVersionsFromFolder("MyApp") } returns expected

        val response =
                client.get("/apps/folder/MyApp/versions") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(expected), response.bodyAsText())
    }

    @Test
    fun `GET app versions from folder - error returns 400`() = testApplication {
        application {
            install(ContentNegotiation) { jackson { registerModule(JavaTimeModule()) } }
            routing {
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        every { service.getAppVersionsFromFolder("MyApp") } throws
                IllegalArgumentException("Invalid app name")

        val response =
                client.get("/apps/folder/MyApp/versions") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid app name", response.bodyAsText())
    }
}
