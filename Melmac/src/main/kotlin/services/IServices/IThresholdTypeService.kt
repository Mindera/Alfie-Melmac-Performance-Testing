package services.IServices

import dtos.ThresholdTypeResponseDTO

interface IThresholdTypeService {
    fun getAll(): List<ThresholdTypeResponseDTO>
    fun getById(id: Int): ThresholdTypeResponseDTO?
    fun getByName(name: String): ThresholdTypeResponseDTO?
}

