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
 *   "ios": {
 *     "simulator": "iPhone 14"
 *   },
 *   "android": {
 *     "emulator": "Pixel_5_API_30"
 *   }
 * }
 * ```
 */
object Config {

    /**
     * The root JSON node containing the entire configuration loaded from the file.
     */
    private val config: JsonNode = ObjectMapper().readTree(File("config.json"))

    /**
     * Retrieves the configuration for a specific platform.
     *
     * @param platform The name of the platform (e.g., "ios", "android").
     * @return A [JsonNode] containing the configuration for the specified platform.
     * @throws IllegalArgumentException If the configuration for the specified platform is not found.
     */
    fun getPlatformConfig(platform: String): JsonNode {
        return config[platform] ?: throw IllegalArgumentException("Configuration for platform '$platform' not found")
    }

    /**
     * Retrieves the server configuration.
     *
     * @return A [JsonNode] containing the server configuration (e.g., port number).
     * @throws IllegalArgumentException If the server configuration is not found.
     */
    fun getServerConfig(): JsonNode {
        return config["server"] ?: throw IllegalArgumentException("Server configuration not found")
    }
}