package services

import domain.TestThreshold
import dtos.TestThresholdRequestDTO
import dtos.TestThresholdResponseDTO
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.IThresholdRepository
import repos.IRepos.IThresholdTypeRepository
import mappers.TestThresholdMapper

class ThresholdServiceTest {

    private val thresholdRepository = mockk<IThresholdRepository>()
    private val thresholdTypeRepository = mockk<IThresholdTypeRepository>()
    private val mapper = mockk<TestThresholdMapper>()
    private val service = ThresholdService(thresholdRepository, thresholdTypeRepository, mapper)

    private val testThreshold = TestThreshold(
        testThresholdId = 1,
        targetValue = 100,
        thresholdTypeThresholdTypeId = 2,
        testPlanVersionTestPlanVersionId = 3,
        metricOutputMetricOutputId = 4
    )

    private val testThresholdResponseDTO = TestThresholdResponseDTO(
        testThresholdId = 1,
        targetValue = 100,
        thresholdTypeThresholdTypeId = 2,
        testPlanVersionTestPlanVersionId = 3,
        metricOutputMetricOutputId = 4
    )

    @Test
    fun `getThresholdByTestPlanVersionId returns list of DTOs`() {
        every { thresholdRepository.findByTestPlanVersionId(3) } returns listOf(testThreshold)
        every { mapper.toDto(testThreshold) } returns testThresholdResponseDTO

        val result = service.getThresholdByTestPlanVersionId(3)

        assertEquals(listOf(testThresholdResponseDTO), result)
        verify(exactly = 1) { thresholdRepository.findByTestPlanVersionId(3) }
        verify(exactly = 1) { mapper.toDto(testThreshold) }
    }

    @Test
    fun `getThresholdById returns DTO when found`() {
        every { thresholdRepository.findById(1) } returns testThreshold
        every { mapper.toDto(testThreshold) } returns testThresholdResponseDTO

        val result = service.getThresholdById(1)

        assertEquals(testThresholdResponseDTO, result)
        verify(exactly = 1) { thresholdRepository.findById(1) }
        verify(exactly = 1) { mapper.toDto(testThreshold) }
    }

    @Test
    fun `getThresholdById returns null when not found`() {
        every { thresholdRepository.findById(99) } returns null

        val result = service.getThresholdById(99)

        assertNull(result)
        verify(exactly = 1) { thresholdRepository.findById(99) }
    }

    @Test
    fun `createTestThreshold creates and returns DTO`() {
        val request = TestThresholdRequestDTO(
            targetValue = 100,
            thresholdType = "MAX",
            testPlanVersionTestPlanVersionId = 3,
            metricOutputMetricOutputId = 4
        )
        val thresholdTypeId = 2
        val newThreshold = testThreshold.copy(testThresholdId = null)
        val savedId = 1

        every { thresholdTypeRepository.findByName("MAX") } returns domain.ThresholdType(
            thresholdTypeId = thresholdTypeId,
            thresholdTypeName = "MAX",
            thresholdTypeDescription = "Maximum"
        )
        every { mapper.fromRequestDto(request, 3, thresholdTypeId) } returns newThreshold
        every { thresholdRepository.save(newThreshold) } returns savedId
        every { mapper.toDto(newThreshold.copy(testThresholdId = savedId)) } returns testThresholdResponseDTO

        val result = service.createTestThreshold(request)

        assertEquals(testThresholdResponseDTO, result)
        verify(exactly = 1) { thresholdTypeRepository.findByName("MAX") }
        verify(exactly = 1) { thresholdRepository.save(newThreshold) }
        verify(exactly = 1) { mapper.fromRequestDto(request, 3, thresholdTypeId) }
        verify(exactly = 1) { mapper.toDto(newThreshold.copy(testThresholdId = savedId)) }
    }

    @Test
    fun `createTestThreshold throws when threshold type not found`() {
        val request = TestThresholdRequestDTO(
            targetValue = 100,
            thresholdType = "UNKNOWN",
            testPlanVersionTestPlanVersionId = 3,
            metricOutputMetricOutputId = 4
        )
        every { thresholdTypeRepository.findByName("UNKNOWN") } returns null

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.createTestThreshold(request)
        }
        assertTrue(exception.message!!.contains("Threshold type not found"))
        verify(exactly = 1) { thresholdTypeRepository.findByName("UNKNOWN") }
    }
}