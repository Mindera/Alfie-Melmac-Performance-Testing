package repos.IRepos

import domain.ThresholdType

/**
 * Repository interface for managing ThresholdType entities.
 */
interface IThresholdTypeRepository {
    fun findAll(): List<ThresholdType>
    fun findByName(name: String): ThresholdType?
    fun findById(thresholdTypeId: Int): ThresholdType?
    fun save(thresholdType: ThresholdType): Int
    fun update(thresholdType: ThresholdType): Int
}