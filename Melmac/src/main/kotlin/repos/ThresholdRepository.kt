package repos

import domain.Threshold
import repos.IRepos.IThresholdRepository

class ThresholdRepository : IThresholdRepository {
    private val thresholds = mutableListOf<Threshold>()
    private var nextId = 1

    override suspend fun getAll(): List<Threshold> = thresholds

    override suspend fun getById(id: Int): Threshold? = thresholds.find { it.id == id }

    override suspend fun create(threshold: Threshold): Threshold {
        val newThreshold = threshold.copy(id = nextId++)
        thresholds.add(newThreshold)
        return newThreshold
    }

    override suspend fun deleteById(id: Int) {
        thresholds.removeIf { it.id == id }
    }
}
