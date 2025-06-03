package services.IServices

import dtos.ThresholdTypeResponseDTO

/**
 * Service interface for managing threshold types.
 * Provides methods to retrieve all threshold types, get a threshold type by ID,
 * and get a threshold type by name.
 */
interface IThresholdTypeService {
    fun getAll(): List<ThresholdTypeResponseDTO>
    fun getById(id: Int): ThresholdTypeResponseDTO?
    fun getByName(name: String): ThresholdTypeResponseDTO?
}

