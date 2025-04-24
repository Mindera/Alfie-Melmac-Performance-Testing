package repos.IRepos

import domain.OS

interface IOSRepository {
    fun findAll(): List<OS>
    fun findByName(name: String): OS?
    fun save(os: OS): Int
}