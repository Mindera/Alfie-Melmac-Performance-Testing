package config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 * Object responsible for loading and providing configuration settings.
 *
 * This object reads the configuration from a `config.json` file located in the root directory
 * and provides methods to retrieve platform-specific and server-specific configurations.
 *
 * Example `config.json` structure:
 * ```
 * {
 *   "server": {
 *     "port": 8080
 *   },
 *  "database": {
 *    "url": "jdbc:mysql://localhost:3306/melmac",
 *    "username": "root",
 *    "password": "password"
 *  }
 * }
 * ```
 */
object Config {

    /**
     * The root JSON node containing the entire configuration loaded from the file.
     */
    private val config: JsonNode = ObjectMapper().readTree(File("config.json"))

    /**
     * Retrieves the server configuration.
     *
     * @return A [JsonNode] containing the server configuration (e.g., port number).
     * @throws IllegalArgumentException If the server configuration is not found.
     */
    fun getServerConfig(): JsonNode {
        return config["server"] ?: throw IllegalArgumentException("Server configuration not found")
    }

    /**
     * Retrieves the database configuration.
     *
     * @return A [JsonNode] containing the database configuration (e.g., URL, username, password).
     * @throws IllegalArgumentException If the database configuration is not found.
     */
    fun getDatabaseConfig(): JsonNode {
        return config["database"] ?: throw IllegalArgumentException("Database configuration not found")
    }
}