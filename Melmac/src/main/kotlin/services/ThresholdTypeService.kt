package services

import dtos.ThresholdTypeResponseDTO
import repos.IRepos.IThresholdTypeRepository
import services.IServices.IThresholdTypeService

class ThresholdTypeService(private val thresholdTypeRepository: IThresholdTypeRepository) :
        IThresholdTypeService {

    override fun getAll(): List<ThresholdTypeResponseDTO> {
        return thresholdTypeRepository.findAll().map {
            ThresholdTypeResponseDTO(
                    thresholdTypeId = it.thresholdTypeId ?: 0,
                    thresholdTypeName = it.thresholdTypeName,
                    thresholdTypeDescription = it.thresholdTypeDescription
            )
        }
    }
}
