package repos

import domain.Metric
import java.sql.Connection
import repos.IRepos.IMetricRepository

class MetricRepository(private val connection: Connection) : IMetricRepository {

    override fun findAll(): List<Metric> {
        val query = "SELECT MetricID, MetricName FROM Metric"
        val statement = connection.prepareStatement(query)
        val resultSet = statement.executeQuery()

        val metrics = mutableListOf<Metric>()
        while (resultSet.next()) {
            metrics.add(
                    Metric(
                            metricId = resultSet.getInt("MetricID"),
                            metricName = resultSet.getString("MetricName")
                    )
            )
        }
        return metrics
    }

    override fun findById(metricId: Int): Metric? {
        val query = "SELECT MetricID, MetricName FROM Metric WHERE MetricID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Metric(
                    metricId = resultSet.getInt("MetricID"),
                    metricName = resultSet.getString("MetricName")
            )
        } else null
    }

    override fun findByName(name: String): Metric? {
        val query = "SELECT MetricID, MetricName FROM Metric WHERE MetricName = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, name)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            Metric(
                    metricId = resultSet.getInt("MetricID"),
                    metricName = resultSet.getString("MetricName")
            )
        } else null
    }

    override fun save(metric: Metric): Int {
        val query = "INSERT INTO Metric (MetricName) VALUES (?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, metric.metricName)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert Metric")
    }
}
