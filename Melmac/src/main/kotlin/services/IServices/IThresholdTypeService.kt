package services.IServices

import dtos.ThresholdTypeResponseDTO

interface IThresholdTypeService {
    fun getAll(): List<ThresholdTypeResponseDTO>
}

