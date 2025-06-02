package services.IServices

/**
 * Service interface for loading and synchronizing data from configuration files.
 * This interface defines methods for syncing data from configuration sources.
 */
interface ILoaderService {
    fun syncDataFromConfig()
}