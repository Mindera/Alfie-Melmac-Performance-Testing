package services

import dtos.ThresholdTypeResponseDTO
import domain.ThresholdType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mappers.ThresholdTypeMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import repos.IRepos.IThresholdTypeRepository

class ThresholdTypeServiceTest {

    private val repo = mockk<IThresholdTypeRepository>()
    private val mapper = mockk<ThresholdTypeMapper>()
    private val service = ThresholdTypeService(repo, mapper)

    private val mockEntity = ThresholdType(
        thresholdTypeId = 1,
        thresholdTypeName = "MAX",
        thresholdTypeDescription = "Maximum threshold"
    )

    private val expectedDTO = ThresholdTypeResponseDTO(
        thresholdTypeId = 1,
        thresholdTypeName = "MAX",
        thresholdTypeDescription = "Maximum threshold"
    )

    @Test
    fun `getAll returns list of DTOs`() {
        every { repo.findAll() } returns listOf(mockEntity)
        every { mapper.toDto(mockEntity) } returns expectedDTO

        val result = service.getAll()

        assertEquals(1, result.size)
        assertEquals(expectedDTO, result[0])
        verify(exactly = 1) { repo.findAll() }
        verify(exactly = 1) { mapper.toDto(mockEntity) }
    }

    @Test
    fun `getById returns DTO when found`() {
        every { repo.findById(1) } returns mockEntity
        every { mapper.toDto(mockEntity) } returns expectedDTO

        val result = service.getById(1)

        assertEquals(expectedDTO, result)
        verify(exactly = 1) { repo.findById(1) }
        verify(exactly = 1) { mapper.toDto(mockEntity) }
    }

    @Test
    fun `getById returns null when not found`() {
        every { repo.findById(99) } returns null

        val result = service.getById(99)

        assertNull(result)
        verify(exactly = 1) { repo.findById(99) }
    }

    @Test
    fun `getByName returns DTO when found`() {
        every { repo.findByName("MAX") } returns mockEntity
        every { mapper.toDto(mockEntity) } returns expectedDTO

        val result = service.getByName("MAX")

        assertEquals(expectedDTO, result)
        verify(exactly = 1) { repo.findByName("MAX") }
        verify(exactly = 1) { mapper.toDto(mockEntity) }
    }

    @Test
    fun `getByName returns null when not found`() {
        every { repo.findByName("UNKNOWN") } returns null

        val result = service.getByName("UNKNOWN")

        assertNull(result)
        verify(exactly = 1) { repo.findByName("UNKNOWN") }
    }
}