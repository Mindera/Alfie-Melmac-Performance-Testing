package repos

import domain.MetricOutput
import java.sql.Connection
import repos.IRepos.IMetricOutputRepository

class MetricOutputRepository(private val connection: Connection) : IMetricOutputRepository {

    override fun findByMetricId(metricId: Int): List<MetricOutput> {
        val query =
                "SELECT MetricOutputID, OutputName, Unit, MetricMetricID FROM MetricOutput WHERE MetricMetricID = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        val resultSet = statement.executeQuery()

        val outputs = mutableListOf<MetricOutput>()
        while (resultSet.next()) {
            outputs.add(
                    MetricOutput(
                            metricOutputId = resultSet.getInt("MetricOutputID"),
                            outputName = resultSet.getString("OutputName"),
                            unit = resultSet.getString("Unit"),
                            metricMetricId = resultSet.getInt("MetricMetricID")
                    )
            )
        }
        return outputs
    }

    override fun findByMetricIdAndName(metricId: Int, outputName: String?): MetricOutput? {
        val query =
                "SELECT MetricOutputID, OutputName, Unit, MetricMetricID FROM MetricOutput WHERE MetricMetricID = ? AND OutputName = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, metricId)
        statement.setString(2, outputName)
        val resultSet = statement.executeQuery()

        return if (resultSet.next()) {
            MetricOutput(
                    metricOutputId = resultSet.getInt("MetricOutputID"),
                    outputName = resultSet.getString("OutputName"),
                    unit = resultSet.getString("Unit"),
                    metricMetricId = resultSet.getInt("MetricMetricID")
            )
        } else null
    }

    override fun save(metricOutput: MetricOutput): Int {
        val query = "INSERT INTO MetricOutput (OutputName, Unit, MetricMetricID) VALUES (?, ?, ?)"
        val statement = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, metricOutput.outputName)
        statement.setString(2, metricOutput.unit)
        statement.setInt(3, metricOutput.metricMetricId)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getInt(1)
        }
        throw IllegalStateException("Failed to insert MetricOutput")
    }

    override fun update(metricOutput: MetricOutput) {
        val query = "UPDATE MetricOutput SET Unit = ? WHERE MetricOutputID = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, metricOutput.unit)
        statement.setInt(2, metricOutput.metricOutputId!!)
        statement.executeUpdate()
    }
}
