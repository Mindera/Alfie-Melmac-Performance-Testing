package services

import domain.ThresholdType
import domain.dtos.ThresholdTypeRequestDTO
import domain.dtos.ThresholdTypeResponseDTO
import repos.IRepos.IThresholdTypeRepository
import services.IServices.IThresholdTypeService

class ThresholdTypeService(
    private val repo: IThresholdTypeRepository
) : IThresholdTypeService {

    override suspend fun getAll(): List<ThresholdTypeResponseDTO> {
        return repo.getAll().map { ThresholdTypeResponseDTO(it.id!!, it.name) }
    }

    override suspend fun create(dto: ThresholdTypeRequestDTO): ThresholdTypeResponseDTO {
        val created = repo.create(ThresholdType(name = dto.name))
        return ThresholdTypeResponseDTO(created.id!!, created.name)
    }
}
