package repos.IRepos

import domain.ThresholdType

interface IThresholdTypeRepository {
    suspend fun getAll(): List<ThresholdType>
    suspend fun getById(id: Int): ThresholdType?
    suspend fun create(type: ThresholdType): ThresholdType
}