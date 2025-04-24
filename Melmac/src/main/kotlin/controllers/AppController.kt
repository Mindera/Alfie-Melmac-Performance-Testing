package controllers

import controllers.IControllers.IAppController
import services.IServices.IAppService
import domain.AppVersion
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import domain.dtos.AppRegistrationRequestDTO
import domain.dtos.AppResponseDTO
import domain.dtos.AppVersionResponseDTO
import org.koin.ktor.ext.inject

class AppController : IAppController {
    override fun Route.routes() {
        val service by inject<IAppService>()

        route("/apps") {
            get {
                val apps = service.listApps()
                val response = apps.map { AppResponseDTO(it.id!!, it.name) }
                call.respond(response)
            }

            get("/{id}/versions") {
                val appId = call.parameters["id"]?.toIntOrNull()
                if (appId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid app ID.")
                    return@get
                }
                val versions = service.listVersions(appId)
                val response = versions.map {
                    AppVersionResponseDTO(
                        id = it.id!!,
                        appId = it.appId,
                        fileName = it.fileName,
                        platform = it.platform,
                        versionName = it.versionName,
                        uploadedAt = it.uploadedAt
                    )
                }
                call.respond(response)
            }

            post("/register") {
                val request = call.receive<AppRegistrationRequestDTO>()
                val version = AppVersion(
                    id = null,
                    appId = -1, // will be filled in service
                    filePath = request.version.filePath,
                    fileName = request.version.fileName,
                    platform = request.version.platform,
                    versionName = request.version.versionName,
                    minSdk = request.version.minSdk,
                    minIosVersion = request.version.minIosVersion
                )

                val saved = service.registerAppAndVersion(request.appName, version)
                call.respond(
                    HttpStatusCode.Created,
                    AppVersionResponseDTO(
                        id = saved.id!!,
                        appId = saved.appId,
                        fileName = saved.fileName,
                        platform = saved.platform,
                        versionName = saved.versionName,
                        uploadedAt = saved.uploadedAt
                    )
                )
            }
        }
    }
}
