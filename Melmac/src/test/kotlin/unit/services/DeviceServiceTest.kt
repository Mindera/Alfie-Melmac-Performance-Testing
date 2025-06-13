import domain.*
import dtos.*
import io.mockk.*
import java.io.File
import mappers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.*
import services.DeviceService

class DeviceServiceTest {

    private val deviceRepository = mockk<IDeviceRepository>()
    private val osVersionRepository = mockk<IOperSysVersionRepository>()
    private val osRepository = mockk<IOperSysRepository>()
    private val availableDeviceMapper = mockk<AvailableDeviceMapper>()
    private val service =
            spyk(
                    DeviceService(
                            deviceRepository,
                            osVersionRepository,
                            osRepository,
                            availableDeviceMapper
                    )
            ) {}

    @Test
    fun `getDeviceById returns DTO when all found`() {
        val device = Device(1, "devName", "serial", 2)
        val osVersion = OSVersion(2, "14.0", 3)
        val os = OperativeSystem(3, "iOS")
        val dto = AvailableDeviceDTO(1, "devName", "serial", "iOS", "14.0")

        every { deviceRepository.findById(1) } returns device
        every { osVersionRepository.findById(2) } returns osVersion
        every { osRepository.findById(3) } returns os
        every { availableDeviceMapper.toDto(device, "iOS", "14.0") } returns dto

        val result = service.getDeviceById(1)
        assertEquals(dto, result)
    }

    @Test
    fun `getDeviceById returns null if device not found`() {
        every { deviceRepository.findById(1) } returns null
        val result = service.getDeviceById(1)
        assertNull(result)
    }

    @Test
    fun `getDeviceById returns null if osVersion not found`() {
        val device = Device(1, "devName", "serial", 2)
        every { deviceRepository.findById(1) } returns device
        every { osVersionRepository.findById(2) } returns null
        val result = service.getDeviceById(1)
        assertNull(result)
    }

    @Test
    fun `getDeviceById returns null if os not found`() {
        val device = Device(1, "devName", "serial", 2)
        val osVersion = OSVersion(2, "14.0", 3)
        every { deviceRepository.findById(1) } returns device
        every { osVersionRepository.findById(2) } returns osVersion
        every { osRepository.findById(3) } returns null
        val result = service.getDeviceById(1)
        assertNull(result)
    }

    @Test
    fun `getAllAvailableDevices returns all devices`() {
        val devices =
                listOf(
                        AvailableDeviceDTO(null, "dev1", "serial1", "iOS", "14.0"),
                        AvailableDeviceDTO(null, "dev2", "serial2", "Android", "30")
                )
        every { service["fetchAllDevices"]() } returns devices
        val result = service.getAllAvailableDevices()
        assertEquals(devices, result)
    }

    @Test
    fun `getAvailableDevicesByMinVersion filters by version`() {
        val devices =
                listOf(
                        AvailableDeviceDTO(null, "dev1", "serial1", "iOS", "13.0"),
                        AvailableDeviceDTO(null, "dev2", "serial2", "iOS", "14.2"),
                        AvailableDeviceDTO(null, "dev3", "serial3", "Android", "29"),
                        AvailableDeviceDTO(null, "dev4", "serial4", "Android", "31")
                )
        every { service["fetchAllDevices"]() } returns devices
        val result = service.getAvailableDevicesByMinVersion("14.0")
        assertTrue(result.any { it.deviceName == "dev2" })
        assertTrue(result.any { it.deviceName == "dev4" })
        assertFalse(result.any { it.deviceName == "dev1" })
        assertTrue(result.any { it.deviceName == "dev3" })
    }

    @Test
    fun `getDeviceBySerialNumber finds correct device`() {
        val devices =
                listOf(
                        AvailableDeviceDTO(null, "dev1", "serial1", "iOS", "14.0"),
                        AvailableDeviceDTO(null, "dev2", "serial2", "Android", "30")
                )
        every { service["fetchAllDevices"]() } returns devices
        val result = service.getDeviceBySerialNumber("serial2")
        assertEquals("dev2", result?.deviceName)
    }

    @Test
    fun `getDeviceByName finds correct device`() {
        val devices =
                listOf(
                        AvailableDeviceDTO(null, "dev1", "serial1", "iOS", "14.0"),
                        AvailableDeviceDTO(null, "dev2", "serial2", "Android", "30")
                )
        every { service["fetchAllDevices"]() } returns devices
        val result = service.getDeviceByName("dev1")
        assertEquals("serial1", result?.deviceSerialNumber)
    }

    @Test
    fun `fetchAllDevices returns combined iOS and Android devices`() {
        val iosDevices = listOf(AvailableDeviceDTO(null, "ios1", "serial-ios1", "iOS", "15.0"))
        val androidDevices =
                listOf(AvailableDeviceDTO(null, "android1", "serial-android1", "Android", "30"))
        every { service["fetchIOSDevices"]() } returns iosDevices
        every { service["fetchAndroidDevices"]() } returns androidDevices

        val result = service.fetchAllDevices()
        assertEquals(2, result.size)
        assertTrue(result.any { it.deviceName == "ios1" })
        assertTrue(result.any { it.deviceName == "android1" })
    }

    @Test
    fun `compareVersions returns positive when version is greater`() {
        val minVersionParts = listOf(14, 0)
        val result = service.compareVersions("14.1", minVersionParts)
        assertTrue(result > 0)
    }

    @Test
    fun `compareVersions returns zero when version is equal`() {
        val minVersionParts = listOf(14, 0)
        val result = service.compareVersions("14.0", minVersionParts)
        assertEquals(0, result)
    }

    @Test
    fun `compareVersions returns negative when version is less`() {
        val minVersionParts = listOf(14, 0)
        val result = service.compareVersions("13.9", minVersionParts)
        assertTrue(result < 0)
    }

    @Test
    fun `fetchIOSDevices returns empty list if not Mac`() {
        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns false

        val result = service.fetchIOSDevices()
        assertTrue(result.isEmpty())

        unmockkObject(utils.Tools)
    }

    @Test
    fun `fetchIOSDevices parses available devices from simctl output`() {
        mockkObject(utils.Tools)
        every { utils.Tools.isMac() } returns true
        val simctlJson =
                """
            {
              "devices": {
                "com.apple.CoreSimulator.SimRuntime.iOS-15-0": [
                  {
                    "name": "iPhone 13",
                    "isAvailable": true,
                    "udid": "ABC-123"
                  },
                  {
                    "name": "iPhone 12",
                    "isAvailable": false,
                    "udid": "DEF-456"
                  }
                ],
                "com.apple.CoreSimulator.SimRuntime.watchOS-8-0": []
              }
            }
        """.trimIndent()
        every { utils.Tools.run(any()) } returns simctlJson

        val result = service.fetchIOSDevices()
        assertEquals(1, result.size)
        assertEquals("iPhone 13", result[0].deviceName)
        assertEquals("ABC-123", result[0].deviceSerialNumber)
        assertEquals("iOS", result[0].osName)
        assertEquals("15.0", result[0].osVersion)

        unmockkObject(utils.Tools)
    }

    @Test
    fun `fetchAndroidDevices returns devices with versions`() {
        val avdNames = listOf("Pixel_5", "Pixel_6")
        every { service.readAVDsFromDirectory() } returns avdNames
        every { service.readAndroidAVDVersion("Pixel_5") } returns "30"
        every { service.readAndroidAVDVersion("Pixel_6") } returns "31"

        val result = service.fetchAndroidDevices()
        assertEquals(2, result.size)
        assertEquals("Pixel_5", result[0].deviceName)
        assertEquals("30", result[0].osVersion)
        assertEquals("Android", result[0].osName)
        assertEquals("Pixel_6", result[1].deviceName)
        assertEquals("31", result[1].osVersion)
    }

    @Test
    fun `readAVDsFromDirectory returns avd names from ini files`() {
        val tempDir = createTempDir()
        val avdDir = File(tempDir, ".android/avd")
        avdDir.mkdirs()
        val ini1 = File(avdDir, "Pixel_5.ini").apply { writeText("AvdId=Pixel_5\n") }
        val ini2 = File(avdDir, "Pixel_6.ini").apply { writeText("AvdId=Pixel_6\n") }
        val ini3 = File(avdDir, "NoAvdId.ini").apply { writeText("SomeOtherKey=foo\n") }
        val oldUserHome = System.getProperty("user.home")
        System.setProperty("user.home", tempDir.absolutePath)

        val serviceLocal =
                DeviceService(
                        deviceRepository,
                        osVersionRepository,
                        osRepository,
                        availableDeviceMapper
                )
        val avds = serviceLocal.readAVDsFromDirectory()
        assertTrue(avds.contains("Pixel_5"))
        assertTrue(avds.contains("Pixel_6"))
        assertTrue(avds.contains("NoAvdId"))

        ini1.delete()
        ini2.delete()
        ini3.delete()
        avdDir.delete()
        File(tempDir, ".android").delete()
        tempDir.delete()
        System.setProperty("user.home", oldUserHome)
    }

    @Test
    fun `readAndroidAVDVersion returns version from ini file`() {
        val tempDir = createTempDir()
        val avdDir = File(tempDir, ".android/avd")
        avdDir.mkdirs()
        val ini = File(avdDir, "Pixel_5.ini").apply { writeText("target=android-30\n") }
        val oldUserHome = System.getProperty("user.home")
        System.setProperty("user.home", tempDir.absolutePath)

        val serviceLocal =
                DeviceService(
                        deviceRepository,
                        osVersionRepository,
                        osRepository,
                        availableDeviceMapper
                )
        val version = serviceLocal.readAndroidAVDVersion("Pixel_5")
        assertEquals("30", version)

        ini.delete()
        avdDir.delete()
        File(tempDir, ".android").delete()
        tempDir.delete()
        System.setProperty("user.home", oldUserHome)
    }

    @Test
    fun `readAndroidAVDVersion returns unknown if ini file does not exist`() {
        val tempDir = createTempDir()
        val oldUserHome = System.getProperty("user.home")
        System.setProperty("user.home", tempDir.parent)

        val serviceLocal =
                DeviceService(
                        deviceRepository,
                        osVersionRepository,
                        osRepository,
                        availableDeviceMapper
                )
        val version = serviceLocal.readAndroidAVDVersion("NonExistentAVD")
        assertEquals("unknown", version)

        tempDir.delete()
        System.setProperty("user.home", oldUserHome)
    }
}
