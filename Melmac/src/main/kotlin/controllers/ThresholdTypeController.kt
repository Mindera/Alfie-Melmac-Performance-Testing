package controllers

import controllers.IControllers.IThresholdTypeController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.IThresholdTypeService

class ThresholdTypeController(
    private val service: IThresholdTypeService
) : IThresholdTypeController {

    override fun Route.routes() {
        route("/threshold-types") {

            get {
                call.respond(service.getAll())
            }
        }
    }
}
