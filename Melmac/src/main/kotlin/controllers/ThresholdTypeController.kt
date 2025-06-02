package controllers

import controllers.IControllers.IThresholdTypeController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import services.IServices.IThresholdTypeService

/**
 * Controller for handling threshold type-related endpoints.
 * Provides routes for retrieving threshold types by ID or name.
 *
 * @property service The service used to interact with threshold type data sources.
 */
class ThresholdTypeController(
    private val service: IThresholdTypeService
) : IThresholdTypeController {

    /**
     * Defines the routes for threshold type-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {
        route("/threshold-types") {
            /**
             * GET /threshold-types
             * Retrieves all threshold types.
             */
            get {
                call.respond(service.getAll())
            }

            /**
             * GET /threshold-types/{id}
             * Retrieves a threshold type by its ID.
             */
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Threshold Type ID")
                    return@get
                }
                val thresholdType = service.getById(id)
                if (thresholdType == null) {
                    call.respond(HttpStatusCode.NotFound, "Threshold Type not found")
                    return@get
                }
                call.respond(thresholdType)
            }

            /**
             * GET /threshold-types/by-name/{name}
             * Retrieves a threshold type by its name.
             */
            get("/by-name/{name}") {
                val name = call.parameters["name"]
                if (name.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Threshold Type Name")
                    return@get
                }
                val thresholdType = service.getByName(name)
                if (thresholdType == null) {
                    call.respond(HttpStatusCode.NotFound, "Threshold Type not found")
                    return@get
                }
                call.respond(thresholdType)
            }
        }
    }
}