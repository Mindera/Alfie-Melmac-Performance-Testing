// filepath: /Users/admin.vasco.sousa/Alfie-Melmac-Performance-Testing/Melmac/src/main/kotlin/config/Config.kt
package config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object Config {
    private val config: JsonNode = ObjectMapper().readTree(File("config.json"))

    fun getPlatformConfig(platform: String): JsonNode {
        return config[platform] ?: throw IllegalArgumentException("Configuration for platform '$platform' not found")
    }

    fun getIosConfig(): JsonNode {
        return getPlatformConfig("ios")
    }

    fun getAndroidConfig(): JsonNode {
        return getPlatformConfig("android")
    }
}