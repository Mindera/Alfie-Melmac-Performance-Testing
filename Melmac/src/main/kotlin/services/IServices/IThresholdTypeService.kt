package services.IServices

import domain.dtos.ThresholdTypeResponseDTO
import domain.dtos.ThresholdTypeRequestDTO

interface IThresholdTypeService {
    suspend fun getAll(): List<ThresholdTypeResponseDTO>
    suspend fun create(dto: ThresholdTypeRequestDTO): ThresholdTypeResponseDTO
}
