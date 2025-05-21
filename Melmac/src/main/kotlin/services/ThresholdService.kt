package services

import domain.Threshold
import domain.dtos.ThresholdRequestDTO
import domain.dtos.ThresholdResponseDTO
import repos.IRepos.IThresholdRepository
import services.IServices.IThresholdService

class ThresholdService(
    private val repo: IThresholdRepository
) : IThresholdService {

    override suspend fun getAll(): List<ThresholdResponseDTO> {
        return repo.getAll().map {
            ThresholdResponseDTO(it.id!!, it.testMetricId, it.thresholdTypeId, it.value)
        }
    }

    override suspend fun create(dto: ThresholdRequestDTO): ThresholdResponseDTO {
        val created = repo.create(
            Threshold(
                testMetricId = dto.testMetricId,
                thresholdTypeId = dto.thresholdTypeId,
                value = dto.value
            )
        )
        return ThresholdResponseDTO(created.id!!, created.testMetricId, created.thresholdTypeId, created.value)
    }

    override suspend fun delete(id: Int) {
        repo.deleteById(id)
    }
}
