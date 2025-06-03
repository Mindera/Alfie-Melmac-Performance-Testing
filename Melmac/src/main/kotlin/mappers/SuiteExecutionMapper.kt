package mappers

import domain.SuiteExecution
import dtos.SuiteExecutionResponseDTO

object SuiteExecutionMapper {
    fun toDto(suiteExecution: SuiteExecution, executionResults: List<dtos.TestExecutionResponseDTO>): SuiteExecutionResponseDTO {
        return SuiteExecutionResponseDTO(
            suiteExecutionId = suiteExecution.suiteExecutionId ?: throw IllegalStateException("SuiteExecution ID cannot be null"),
            initialTimestamp = suiteExecution.initialTimestamp,
            endTimestamp = suiteExecution.endTimestamp,
            testSuiteVersionTestSuiteVersionId = suiteExecution.testSuiteVersionTestSuiteVersionId,
            executionResults = executionResults
        )
    }

    fun toDomain(dto: SuiteExecutionResponseDTO): SuiteExecution {
        return SuiteExecution(
            suiteExecutionId = dto.suiteExecutionId,
            initialTimestamp = dto.initialTimestamp,
            endTimestamp = dto.endTimestamp,
            testSuiteVersionTestSuiteVersionId = dto.testSuiteVersionTestSuiteVersionId
        )
    }
}