package services

import dtos.ThresholdTypeResponseDTO
import repos.IRepos.IThresholdTypeRepository
import services.IServices.IThresholdTypeService
import mappers.ThresholdTypeMapper

/**
 * Service implementation for managing Threshold Types.
 *
 * Provides methods to retrieve all threshold types, or a specific threshold type by ID or name.
 *
 * @property thresholdTypeRepository Repository for ThresholdType entities.
 */
class ThresholdTypeService(private val thresholdTypeRepository: IThresholdTypeRepository) :
        IThresholdTypeService {

    /**
     * Retrieves all threshold types.
     *
     * @return List of [ThresholdTypeResponseDTO] representing all threshold types.
     */
    override fun getAll(): List<ThresholdTypeResponseDTO> {
        return thresholdTypeRepository.findAll().map {
            ThresholdTypeMapper.toDto(it)
        }
    }

    /**
     * Retrieves a threshold type by its ID.
     *
     * @param id The ID of the threshold type.
     * @return [ThresholdTypeResponseDTO] for the specified threshold type, or null if not found.
     */
    override fun getById(id: Int): ThresholdTypeResponseDTO? {
        return thresholdTypeRepository.findById(id)?.let {
            ThresholdTypeMapper.toDto(it)
        }
    }

    /**
     * Retrieves a threshold type by its name.
     *
     * @param name The name of the threshold type.
     * @return [ThresholdTypeResponseDTO] for the specified threshold type, or null if not found.
     */
    override fun getByName(name: String): ThresholdTypeResponseDTO? {
        return thresholdTypeRepository.findByName(name)?.let {
            ThresholdTypeMapper.toDto(it)
        }
    }
}