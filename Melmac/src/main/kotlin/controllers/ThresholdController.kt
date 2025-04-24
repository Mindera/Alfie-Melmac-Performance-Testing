package controllers

import controllers.IControllers.IThresholdController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import domain.dtos.ThresholdRequestDTO
import services.IServices.IThresholdService

class ThresholdController(
    private val service: IThresholdService
) : IThresholdController {

    override fun Route.routes() {
        route("/thresholds") {
            get {
                call.respond(service.getAll())
            }
            post {
                val dto = call.receive<ThresholdRequestDTO>()
                call.respond(service.create(dto))
            }
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respondText("Invalid ID", status = io.ktor.http.HttpStatusCode.BadRequest)
                } else {
                    service.delete(id)
                    call.respondText("Deleted")
                }
            }
        }
    }
}
