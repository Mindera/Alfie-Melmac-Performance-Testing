import domain.*
import dtos.*
import repos.IRepos.*
import services.AppService
import io.mockk.*
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths

class AppServiceTest {

    private val appRepository = mockk<IAppRepository>()
    private val appVersionRepository = mockk<IAppVersionRepository>()
    private val appMapper = mockk<AppMapper>()
    private val appVersionMapper = mockk<AppVersionMapper>()
    private val service = AppService(appRepository, appVersionRepository, appMapper, appVersionMapper)

    // --- DB Methods ---

    @Test
    fun `getAllAppsFromDatabase returns mapped DTOs`() {
        val apps = listOf(App(1, "App1"), App(2, "App2"))
        val dtos = listOf(AppResponseDTO(1, "App1"), AppResponseDTO(2, "App2"))
        every { appRepository.findAll() } returns apps
        every { appMapper.toDto(apps[0]) } returns dtos[0]
        every { appMapper.toDto(apps[1]) } returns dtos[1]

        val result = service.getAllAppsFromDatabase()
        assertEquals(dtos, result)
    }

    @Test
    fun `getAppVersionsByAppIdFromDatabase returns mapped versions`() {
        val app = App(1, "App1")
        val versions = listOf(AppVersion(1, 1, "1.0"), AppVersion(2, 1, "2.0"))
        val dtos = listOf(
            AppVersionResponseDTO(1, 1, "1.0"),
            AppVersionResponseDTO(2, 1, "2.0")
        )
        every { appRepository.findById(1) } returns app
        every { appVersionRepository.findByAppId(1) } returns versions
        every { appVersionMapper.toDto(versions[0]) } returns dtos[0]
        every { appVersionMapper.toDto(versions[1]) } returns dtos[1]

        val result = service.getAppVersionsByAppIdFromDatabase(1)
        assertEquals(dtos, result)
    }

    @Test
    fun `getAppVersionsByAppIdFromDatabase throws if app not found`() {
        every { appRepository.findById(1) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            service.getAppVersionsByAppIdFromDatabase(1)
        }
    }

    @Test
    fun `getAppByIdFromDatabase returns mapped DTO`() {
        val app = App(1, "App1")
        val dto = AppResponseDTO(1, "App1")
        every { appRepository.findById(1) } returns app
        every { appMapper.toDto(app) } returns dto

        val result = service.getAppByIdFromDatabase(1)
        assertEquals(dto, result)
    }

    @Test
    fun `getAppByIdFromDatabase throws if not found`() {
        every { appRepository.findById(1) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            service.getAppByIdFromDatabase(1)
        }
    }

    @Test
    fun `getAppVersionByIdFromDatabase returns mapped DTO`() {
        val appVersion = AppVersion(1, 1, "1.0")
        val dto = AppVersionResponseDTO(1, 1, "1.0")
        every { appVersionRepository.findById(1) } returns appVersion
        every { appVersionMapper.toDto(appVersion) } returns dto

        val result = service.getAppVersionByIdFromDatabase(1)
        assertEquals(dto, result)
    }

    @Test
    fun `getAppVersionByIdFromDatabase throws if not found`() {
        every { appVersionRepository.findById(1) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            service.getAppVersionByIdFromDatabase(1)
        }
    }

    @Test
    fun `getAppByVersionIdFromDatabase returns mapped DTO`() {
        val appVersion = AppVersion(1, 2, "1.0")
        val app = App(2, "App2")
        val dto = AppResponseDTO(2, "App2")
        every { appVersionRepository.findById(1) } returns appVersion
        every { appRepository.findById(2) } returns app
        every { appMapper.toDto(app) } returns dto

        val result = service.getAppByVersionIdFromDatabase(1)
        assertEquals(dto, result)
    }

    @Test
    fun `getAppByVersionIdFromDatabase throws if appVersion not found`() {
        every { appVersionRepository.findById(1) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            service.getAppByVersionIdFromDatabase(1)
        }
    }

    @Test
    fun `getAppByVersionIdFromDatabase throws if app not found`() {
        val appVersion = AppVersion(1, 2, "1.0")
        every { appVersionRepository.findById(1) } returns appVersion
        every { appRepository.findById(2) } returns null
        assertThrows(IllegalArgumentException::class.java) {
            service.getAppByVersionIdFromDatabase(1)
        }
    }

    // --- Folder Methods ---

    @Test
    fun `getAllAppsFromFolder returns mapped DTOs for files`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val apk = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        every { appMapper.toDto(any()) } returns AppResponseDTO(1, "TestApp.apk")
        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        val result = service.getAllAppsFromFolder()
        assertEquals(1, result.size)
        assertEquals("TestApp.apk", result[0].appName)

        apk.delete()
        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }

    @Test
    fun `getAllAppsFromFolder throws if folder missing`() {
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", "/tmp/doesnotexist")
        assertThrows(IllegalArgumentException::class.java) {
            service.getAllAppsFromFolder()
        }
        System.setProperty("user.dir", oldUserDir)
    }

    @Test
    fun `getAppByNameFromFolder returns mapped DTO if found`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val apk = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        every { appMapper.toDto(any()) } returns AppResponseDTO(1, "TestApp.apk")
        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        val result = service.getAppByNameFromFolder("TestApp.apk")
        assertEquals("TestApp.apk", result.appName)

        apk.delete()
        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }

    @Test
    fun `getAppByNameFromFolder throws if not found`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        assertThrows(IllegalArgumentException::class.java) {
            service.getAppByNameFromFolder("NotThere.apk")
        }

        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }

    @Test
    fun `getAppVersionsFromFolder returns mapped versions`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val apk = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false
        every { utils.Tools.getApkVersion(any()) } returns "1.2.3"
        every { appVersionMapper.toDto(any()) } returns AppVersionResponseDTO(1, 1, "1.2.3")

        val result = service.getAppVersionsFromFolder("TestApp.apk")
        assertEquals(1, result.size)
        assertEquals("1.2.3", result[0].appVersion)

        apk.delete()
        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }

    @Test
    fun `getAppVersionsFromFolder throws if not found`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        assertThrows(IllegalArgumentException::class.java) {
            service.getAppVersionsFromFolder("NotThere.apk")
        }

        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }

    @Test
    fun `getAppVersionByNameFromFolder returns mapped DTO`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val apk = File(appsDir, "TestApp.apk").apply { writeText("dummy") }
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        every { appVersionMapper.toDto(any()) } returns AppVersionResponseDTO(1, 1, "1.2.3")
        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        val result = service.getAppVersionByNameFromFolder("TestApp.apk", "1.2.3")
        assertEquals("1.2.3", result.appVersion)

        apk.delete()
        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }

    @Test
    fun `getAppVersionByNameFromFolder throws if not found`() {
        val tempDir = createTempDir()
        val appsDir = File(tempDir, "src/main/resources/apps")
        appsDir.mkdirs()
        val oldUserDir = System.getProperty("user.dir")
        System.setProperty("user.dir", tempDir.absolutePath)

        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        assertThrows(IllegalArgumentException::class.java) {
            service.getAppVersionByNameFromFolder("NotThere.apk", "1.2.3")
        }

        appsDir.delete()
        File(tempDir, "src/main/resources").delete()
        File(tempDir, "src/main").delete()
        tempDir.delete()
        System.setProperty("user.dir", oldUserDir)
        unmockkObject(utils.Tools)
    }
}