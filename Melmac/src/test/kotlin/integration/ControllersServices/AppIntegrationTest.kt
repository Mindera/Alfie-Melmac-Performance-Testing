import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import controllers.AppController
import controllers.IControllers.IAppController
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
import java.io.File
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import repos.IRepos.*
import services.AppService
import services.IServices.IAppService
import utils.Tools

class AppIntegrationTest : KoinTest {

    companion object {
        private val appRepo = mockk<IAppRepository>()
        private val versionRepo = mockk<IAppVersionRepository>()
        private val appMapper = mockk<AppMapper>()
        private val versionMapper = mockk<AppVersionMapper>()

        private val service = AppService(appRepo, versionRepo, appMapper, versionMapper)

        val module = module {
            single<IAppRepository> { appRepo }
            single<IAppVersionRepository> { versionRepo }
            single<AppMapper> { appMapper }
            single<AppVersionMapper> { versionMapper }
            single<IAppService> { service }
            single<IAppController> { AppController(get()) }
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
                val controller = get<IAppController>()
                with(controller) { routes() }
            }
        }
        builder()
    }

    @Test
    fun getAllAppsFromDatabaseReturnsMappedDTOs() = testRoutes {
        val apps = listOf(App(1, "App1"), App(2, "App2"))
        val dtos = listOf(AppResponseDTO(1, "App1"), AppResponseDTO(2, "App2"))
        every { appRepo.findAll() } returns apps
        every { appMapper.toDto(apps[0]) } returns dtos[0]
        every { appMapper.toDto(apps[1]) } returns dtos[1]

        val response = client.get("/apps/db")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dtos), response.bodyAsText())
    }

    @Test
    fun getAppVersionsByAppIdReturnsMappedVersions() = testRoutes {
        val app = App(1, "App1")
        val versions = listOf(AppVersion(1, 1, "1.0"), AppVersion(2, 1, "2.0"))
        val dtos = listOf(AppVersionResponseDTO(1, 1, "1.0"), AppVersionResponseDTO(2, 1, "2.0"))
        every { appRepo.findById(1) } returns app
        every { versionRepo.findByAppId(1) } returns versions
        every { versionMapper.toDto(versions[0]) } returns dtos[0]
        every { versionMapper.toDto(versions[1]) } returns dtos[1]

        val response = client.get("/apps/db/1/versions")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dtos), response.bodyAsText())
    }

    @Test
    fun getAppVersionsByAppIdThrowsIfAppNotFound() = testRoutes {
        every { appRepo.findById(1) } returns null

        val response = client.get("/apps/db/1/versions")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun getAppByIdReturnsMappedDTO() = testRoutes {
        val app = App(1, "App1")
        val dto = AppResponseDTO(1, "App1")
        every { appRepo.findById(1) } returns app
        every { appMapper.toDto(app) } returns dto

        val response = client.get("/apps/db/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dto), response.bodyAsText())
    }

    @Test
    fun getAppByIdThrowsIfNotFound() = testRoutes {
        every { appRepo.findById(1) } returns null

        val response = client.get("/apps/db/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun getAppVersionByIdReturnsMappedDTO() = testRoutes {
        val version = AppVersion(1, 1, "1.0")
        val dto = AppVersionResponseDTO(1, 1, "1.0")
        every { versionRepo.findById(1) } returns version
        every { versionMapper.toDto(version) } returns dto

        val response = client.get("/apps/db/version/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dto), response.bodyAsText())
    }

    @Test
    fun getAppVersionByIdThrowsIfNotFound() = testRoutes {
        every { versionRepo.findById(1) } returns null

        val response = client.get("/apps/db/version/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun getAppByVersionIdReturnsMappedDTO() = testRoutes {
        val version = AppVersion(1, 2, "1.0")
        val app = App(2, "App2")
        val dto = AppResponseDTO(2, "App2")
        every { versionRepo.findById(1) } returns version
        every { appRepo.findById(2) } returns app
        every { appMapper.toDto(app) } returns dto

        val response = client.get("/apps/db/appByVersionId/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(objectMapper.writeValueAsString(dto), response.bodyAsText())
    }

    @Test
    fun getAppByVersionIdThrowsIfVersionNotFound() = testRoutes {
        every { versionRepo.findById(1) } returns null

        val response = client.get("/apps/db/appByVersionId/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun getAppByVersionIdThrowsIfAppNotFound() = testRoutes {
        val version = AppVersion(1, 2, "1.0")
        every { versionRepo.findById(1) } returns version
        every { appRepo.findById(2) } returns null

        val response = client.get("/apps/db/appByVersionId/1")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun getAllAppsFromFolderReturnsMappedDTOsForFiles() = testRoutes {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps").apply { mkdirs() }
        val apkFile = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(Tools)
        every { Tools.isMac() } returns false
        every { appMapper.toDto(any()) } returns AppResponseDTO(1, "TestApp.apk")

        val response = client.get("/apps/folder")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            objectMapper.writeValueAsString(listOf(AppResponseDTO(1, "TestApp.apk"))),
            response.bodyAsText()
        )

        apkFile.delete()
        appsDir.deleteRecursively()
        tempDir.deleteRecursively()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(Tools)
    }

    @Test
    fun getAppVersionsFromFolderReturnsMappedVersions() = testRoutes {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps").apply { mkdirs() }
        val apk = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(Tools)
        every { Tools.isMac() } returns false
        every { Tools.getApkVersion(any()) } returns "1.2.3"
        every { versionMapper.toDto(any()) } returns AppVersionResponseDTO(1, 1, "1.2.3")

        val response =
                client.get("/apps/folder/TestApp.apk/versions") {
                    header(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("1.2.3"))

        apk.delete()
        appsDir.deleteRecursively()
        tempDir.deleteRecursively()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(Tools)
    }

    @Test
    fun getAppVersionsFromFolderThrowsIfNotFound() = testRoutes {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps").apply { mkdirs() }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(Tools)
        every { Tools.isMac() } returns false

        val response = client.get("/apps/folder/NotThere.apk/versions")
        assertEquals(HttpStatusCode.BadRequest, response.status)

        appsDir.deleteRecursively()
        tempDir.deleteRecursively()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(Tools)
    }

    @Test
    fun getAppVersionByNameFromFolderReturnsMappedDTO() = testRoutes {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps").apply { mkdirs() }
        val apk = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(Tools)
        every { Tools.isMac() } returns false
        every { versionMapper.toDto(any()) } returns AppVersionResponseDTO(1, 1, "1.2.3")

        val response = client.get("/apps/folder/TestApp.apk/versions")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("1.2.3"))

        apk.delete()
        appsDir.deleteRecursively()
        tempDir.deleteRecursively()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(Tools)
    }

    @Test
    fun getAppVersionByNameFromFolderThrowsIfNotFound() = testRoutes {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps").apply { mkdirs() }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(Tools)
        every { Tools.isMac() } returns false

        val response = client.get("/apps/folder/NotThere.apk/versions")
        assertEquals(HttpStatusCode.BadRequest, response.status)

        appsDir.deleteRecursively()
        tempDir.deleteRecursively()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(Tools)
    }
}
