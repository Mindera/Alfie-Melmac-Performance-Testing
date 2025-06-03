package repos.IRepos

import java.time.Instant

/**
 * Interface for managing bootstrap update records in the database.
 *
 * Provides methods to retrieve the latest update date and save a new update date.
 */
interface IBootstrapUpdateRepository {
    fun getLatestUpdateDate(): Instant?
    fun save(updateDate: Instant): Boolean
}
