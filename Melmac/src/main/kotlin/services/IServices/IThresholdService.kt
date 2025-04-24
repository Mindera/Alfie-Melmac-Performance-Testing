package services.IServices

import domain.dtos.ThresholdRequestDTO
import domain.dtos.ThresholdResponseDTO

interface IThresholdService {
    suspend fun getAll(): List<ThresholdResponseDTO>
    suspend fun create(dto: ThresholdRequestDTO): ThresholdResponseDTO
    suspend fun delete(id: Int)
}
