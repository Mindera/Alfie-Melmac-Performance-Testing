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

/**
 * Controller for handling threshold-related endpoints.
 * Provides routes for retrieving and creating test thresholds.
 *
 * @property thresholdService The service used to interact with threshold data sources.
 */
class ThresholdController(
    private val thresholdService: IThresholdService
) : IThresholdController {

    /**
     * Defines the routes for threshold-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/thresholds") {

            /**
             * GET /thresholds
             * Retrieves all thresholds for a given test plan version.
             */
            get {
                val testPlanVersionId = call.request.queryParameters["testPlanVersionId"]?.toIntOrNull()
                if (testPlanVersionId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid testPlanVersionId")
                    return@get
                }
                val thresholds: List<TestThresholdResponseDTO> = thresholdService.getThresholdByTestPlanVersionId(testPlanVersionId)
                call.respond(thresholds)
            }

            /**
             * GET /thresholds/{id}
             * Retrieves a specific threshold by its ID.
             */
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

            /**
             * POST /thresholds
             * Creates a new threshold.
             */
            post {
                val thresholdRequest = call.receive<TestThresholdRequestDTO>()
                val createdThreshold = thresholdService.createTestThreshold(thresholdRequest)
                call.respond(HttpStatusCode.Created, createdThreshold)
            }
        }
    }
}