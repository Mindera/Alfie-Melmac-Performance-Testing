package core

interface AppManager {
    fun installApp(appPath: String)
    fun uninstallApp(appId: String)
}