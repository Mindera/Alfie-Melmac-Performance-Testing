package controllers

import controllers.IControllers.IThresholdController
import dtos.TestThresholdResponseDTO
import dtos.TestThresholdRequestDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import services.IServices.IThresholdService

class ThresholdController(
    private val thresholdService: IThresholdService
) : IThresholdController {

    override fun Route.routes() {

        route("/thresholds") {

            get {
                val testPlanVersionId = call.request.queryParameters["testPlanVersionId"]?.toIntOrNull()
                if (testPlanVersionId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid testPlanVersionId")
                    return@get
                }
                val thresholds: List<TestThresholdResponseDTO> = thresholdService.getThresholdByTestPlanVersionId(testPlanVersionId)
                call.respond(thresholds)
            }

            // Endpoint para obter um threshold específico
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Threshold ID")
                    return@get
                }

                val threshold: TestThresholdResponseDTO? = thresholdService.getThresholdById(id)
                if (threshold == null) {
                    call.respond(HttpStatusCode.NotFound, "Threshold not found")
                    return@get
                }

                call.respond(threshold)
            }

            // Endpoint para criar um novo threshold
            post {
                val thresholdRequest = call.receive<TestThresholdRequestDTO>()
                val createdThreshold = thresholdService.createTestThreshold(thresholdRequest)
                call.respond(HttpStatusCode.Created, createdThreshold)
            }
        }
    }
}
