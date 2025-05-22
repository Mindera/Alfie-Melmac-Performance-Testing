package services

import services.IServices.IAppService
import repos.IRepos.*
import domain.App
import domain.AppVersion

class AppService(
    private val appRepo: IAppRepository,
    private val versionRepo: IAppVersionRepository
) : IAppService {

    override fun registerAppAndVersion(appName: String, version: AppVersion): AppVersion {
        val app = appRepo.findByName(appName) ?: run {
            val id = appRepo.save(App(name = appName))
            App(id = id, name = appName)
        }

        val existing = versionRepo.findByAppIdAndVersionName(app.id!!, version.versionName)
        if (existing != null) return existing

        val versionWithApp = version.copy(appId = app.id)
        val id = versionRepo.save(versionWithApp)
        return versionWithApp.copy(id = id)
    }

    override fun listApps(): List<App> = appRepo.findAll()
    override fun listVersions(appId: Int): List<AppVersion> = versionRepo.findByAppId(appId)
    override fun getAppById(id: Int): App? = appRepo.findById(id)
    override fun getVersionById(id: Int): AppVersion? = versionRepo.findById(id)
}
