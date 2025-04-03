package core

/**
 * Interface for managing applications on a device or emulator.
 * Provides methods to install and uninstall applications.
 */
interface AppManager {

    /**
     * Installs an application on the connected device or emulator.
     *
     * @param appPath The file path to the application to be installed (e.g., APK for Android, .app for iOS).
     */
    fun installApp(appPath: String)

    /**
     * Uninstalls an application from the connected device or emulator.
     *
     * @param appId The unique identifier (e.g., package name for Android, bundle identifier for iOS) of the application to be uninstalled.
     */
    fun uninstallApp(appId: String)
}