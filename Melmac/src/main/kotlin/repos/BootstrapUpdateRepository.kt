package repos

import java.sql.Connection
import java.sql.ResultSet
import java.time.Instant
import repos.IRepos.IBootstrapUpdateRepository

class BootstrapUpdateRepository(private val connection: Connection) : IBootstrapUpdateRepository {

    /**
     * Returns the latest UpdateDate from the BootstrapUpdate table, or null if the table is empty.
     */
    override fun getLatestUpdateDate(): Instant? {
        val sql = "SELECT TOP 1 UpdateDate FROM BootstrapUpdate ORDER BY UpdateDate DESC"
        connection.prepareStatement(sql).use { stmt ->
            val rs: ResultSet = stmt.executeQuery()
            return if (rs.next()) {
                rs.getTimestamp("UpdateDate").toInstant()
            } else {
                null
            }
        }
    }

    /**
     * Saves a new update date to the BootstrapUpdate table.
     *
     * @param updateDate The Instant representing the update date to save.
     * @return true if the operation was successful, false otherwise.
     */
    override fun save(updateDate: Instant): Boolean {
        val sql = "INSERT INTO BootstrapUpdate (UpdateDate) VALUES (?)"
        connection.prepareStatement(sql).use { stmt ->
            stmt.setTimestamp(1, java.sql.Timestamp.from(updateDate))
            return stmt.executeUpdate() > 0
        }
    }
}
