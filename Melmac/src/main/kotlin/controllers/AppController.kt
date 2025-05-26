package controllers

import controllers.IControllers.IAppController
import dtos.AppResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.IAppService

class AppController(private val appService: IAppService) : IAppController {

    override fun Route.routes() {

        route("/apps") {
            
            // --- Database endpoints ---

            get("/db") {
                val apps: List<AppResponseDTO> = appService.getAllAppsFromDatabase()
                call.respond(apps)
            }

            get("/db/{appId}/versions") {
                val appId = call.parameters["appId"]?.toIntOrNull()
                if (appId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid App ID")
                    return@get
                }

                try {
                    val versions = appService.getAppVersionsByAppIdFromDatabase(appId)
                    call.respond(versions)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "App not found")
                }
            }

            // --- Folder endpoints ---

            get("/folder") {
                try {
                    val apps: List<AppResponseDTO> = appService.getAllAppsFromFolder()
                    call.respond(apps)
                } catch (e: IllegalArgumentException) {
                    call.respond(
                            HttpStatusCode.BadRequest,
                            e.message
                                    ?: "Failed to retrieve apps. Please check the folder path and try again."
                    )
                }
            }

            get("/folder/{appName}/versions") {
                val appName = call.parameters["appName"]

                if (appName.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'appName' path parameter")
                    return@get
                }

                try {
                    val versions = appService.getAppVersionsFromFolder(appName)
                    call.respond(versions)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid app name")
                }
            }
        }
    }
}
