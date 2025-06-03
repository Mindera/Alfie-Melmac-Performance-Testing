package controllers

import controllers.IControllers.IAppController
import dtos.AppResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.IAppService

/**
 * Controller for handling app-related endpoints.
 * Provides routes for retrieving app and app version information from both the database and the folder.
 *
 * @property appService The service used to interact with app data sources.
 */
class AppController(private val appService: IAppService) : IAppController {

    /**
     * Defines the routes for app-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/apps") {
            
            // --- Database endpoints ---

            /**
             * GET /apps/db
             * Retrieves all apps from the database.
             */
            get("/db") {
                val apps: List<AppResponseDTO> = appService.getAllAppsFromDatabase()
                call.respond(apps)
            }

            /**
             * GET /apps/db/{appId}
             * Retrieves a specific app by its ID from the database.
             */
            get("/db/{appId}") {
                val appId = call.parameters["appId"]?.toIntOrNull()
                if (appId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid App ID")
                    return@get
                }

                try {
                    val app = appService.getAppByIdFromDatabase(appId)
                    call.respond(app)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "App not found")
                }
            }

            /**
             * GET /apps/db/{appId}/versions
             * Retrieves all versions for a specific app from the database.
             */
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

            /**
             * GET /apps/db/appByVersionId/{appVersionId}
             * Retrieves an app by its version ID from the database.
             */
            get("/db/appByVersionId/{appVersionId}") {
                val appVersionId = call.parameters["appVersionId"]?.toIntOrNull()
                if (appVersionId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid App Version ID")
                    return@get
                }

                try {
                    val app = appService.getAppByVersionIdFromDatabase(appVersionId)
                    call.respond(app)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "App not found")
                }
            }

            /**
             * GET /apps/db/version/{appVersionId}
             * Retrieves a specific app version by its ID from the database.
             */
            get("/db/version/{appVersionId}") {
                val appVersionId = call.parameters["appVersionId"]?.toIntOrNull()
                if (appVersionId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid App Version ID")
                    return@get
                }

                try {
                    val appVersion = appService.getAppVersionByIdFromDatabase(appVersionId)
                    call.respond(appVersion)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.NotFound, e.message ?: "App Version not found")
                }
            }

            // --- Folder endpoints ---

            /**
             * GET /apps/folder
             * Retrieves all apps from the folder.
             */
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

            /**
             * GET /apps/folder/{appName}/versions
             * Retrieves all versions for a specific app from the folder.
             */
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