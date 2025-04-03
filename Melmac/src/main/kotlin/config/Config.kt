package config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

/**
 * Object responsible for loading and providing configuration settings.
 * Reads the configuration from a `config.json` file and provides platform-specific configurations.
 */
object Config {

    // The root JSON node containing the entire configuration loaded from the file
    private val config: JsonNode = ObjectMapper().readTree(File("config.json"))

    /**
     * Retrieves the configuration for a specific platform.
     *
     * @param platform The name of the platform (e.g., "ios", "android").
     * @return A [JsonNode] containing the configuration for the specified platform.
     * @throws IllegalArgumentException if the configuration for the specified platform is not found.
     */
    fun getPlatformConfig(platform: String): JsonNode {
        return config[platform] ?: throw IllegalArgumentException("Configuration for platform '$platform' not found")
    }
}