package controllers

import controllers.IControllers.IThresholdTypeController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import services.IServices.IThresholdTypeService

class ThresholdTypeController(
    private val service: IThresholdTypeService
) : IThresholdTypeController {

    override fun Route.routes() {
        route("/threshold-types") {
            get {
                call.respond(service.getAll())
            }

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
