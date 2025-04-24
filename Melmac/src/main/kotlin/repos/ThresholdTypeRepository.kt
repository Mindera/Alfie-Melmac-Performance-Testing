package repos

import domain.ThresholdType
import repos.IRepos.IThresholdTypeRepository

class ThresholdTypeRepository : IThresholdTypeRepository {
    private val types = mutableListOf<ThresholdType>()
    private var nextId = 1

    override suspend fun getAll(): List<ThresholdType> = types

    override suspend fun getById(id: Int): ThresholdType? = types.find { it.id == id }

    override suspend fun create(type: ThresholdType): ThresholdType {
        val newType = type.copy(id = nextId++)
        types.add(newType)
        return newType
    }
}