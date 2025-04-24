package repos.IRepos

import domain.Threshold

interface IThresholdRepository {
    suspend fun getAll(): List<Threshold>
    suspend fun getById(id: Int): Threshold?
    suspend fun create(threshold: Threshold): Threshold
    suspend fun deleteById(id: Int)
}
