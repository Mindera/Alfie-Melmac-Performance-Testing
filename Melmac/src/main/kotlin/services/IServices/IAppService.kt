package services.IServices

import domain.App
import domain.AppVersion

interface IAppService {
    fun registerAppAndVersion(appName: String, version: AppVersion): AppVersion
    fun listApps(): List<App>
    fun listVersions(appId: Int): List<AppVersion>
    fun getAppById(id: Int): App?
    fun getVersionById(id: Int): AppVersion?
}