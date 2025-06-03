package mappers

import domain.ThresholdType
import dtos.ThresholdTypeResponseDTO

object ThresholdTypeMapper {
    fun toDto(thresholdType: ThresholdType): ThresholdTypeResponseDTO {
        return ThresholdTypeResponseDTO(
                thresholdTypeId = thresholdType.thresholdTypeId
                                ?: throw IllegalStateException("ThresholdType ID cannot be null"),
                thresholdTypeName = thresholdType.thresholdTypeName,
                thresholdTypeDescription = thresholdType.thresholdTypeDescription
        )
    }

    fun toDomain(dto: ThresholdTypeResponseDTO): ThresholdType {
        return ThresholdType(
                thresholdTypeId = dto.thresholdTypeId,
                thresholdTypeName = dto.thresholdTypeName,
                thresholdTypeDescription = dto.thresholdTypeDescription
        )
    }
}
