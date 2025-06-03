package mappers

import domain.TestSuite
import dtos.TestSuiteResponseDTO
import dtos.TestSuiteRequestDTO
import java.time.LocalDateTime

object TestSuiteMapper {
    fun toDto(testSuite: TestSuite): TestSuiteResponseDTO {
        return TestSuiteResponseDTO(
            testSuiteId = testSuite.testSuiteId ?: throw IllegalStateException("TestSuite ID cannot be null"),
            testSuiteName = testSuite.testSuiteName,
            testSuiteDescription = testSuite.testSuiteDescription,
            creationTimestamp = testSuite.creationTimestamp,
            isActive = testSuite.isActive
        )
    }

    fun toDomain(dto: TestSuiteResponseDTO): TestSuite {
        return TestSuite(
            testSuiteId = dto.testSuiteId,
            testSuiteName = dto.testSuiteName,
            testSuiteDescription = dto.testSuiteDescription,
            creationTimestamp = dto.creationTimestamp,
            isActive = dto.isActive
        )
    }

    fun fromRequestDto(dto: TestSuiteRequestDTO, creationTimestamp: LocalDateTime, isActive: Boolean): TestSuite {
        return TestSuite(
            testSuiteName = dto.testSuiteName,
            testSuiteDescription = dto.testSuiteDescription,
            creationTimestamp = creationTimestamp,
            isActive = isActive
        )
    }
}