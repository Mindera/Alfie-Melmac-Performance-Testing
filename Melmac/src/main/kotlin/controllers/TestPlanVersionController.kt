package controllers

import controllers.IControllers.ITestPlanVersionController
import dtos.TestPlanVersionRequestDTO
import dtos.TestPlanVersionResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.ITestPlanVersionService

/**
 * Controller for handling test plan version-related endpoints.
 * Provides routes for retrieving test plan versions and their details.
 *
 * @property testPlanVersionService The service used to interact with test plan version data sources.
 */
class TestPlanVersionController(
    private val testPlanVersionService: ITestPlanVersionService
) : ITestPlanVersionController {

    /**
     * Defines the routes for test plan version-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {
        route("/test-plan-versions") {

            /**
             * GET /test-plan-versions/{id}
             * Retrieves a specific test plan version by ID.
             */
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan Version ID")
                    return@get
                }
                val version = testPlanVersionService.getTestPlanVersionById(id)
                if (version == null) {
                    call.respond(HttpStatusCode.NotFound, "Test Plan Version not found")
                    return@get
                }
                call.respond(version)
            }

            /**
             * GET /test-plan-versions/by-test-plan/{testPlanId}
             * Retrieves all versions for a specific test plan.
             */
            get("/by-test-plan/{testPlanId}") {
                val testPlanId = call.parameters["testPlanId"]?.toIntOrNull()
                if (testPlanId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@get
                }
                val versions = testPlanVersionService.getTestPlanVersionsByTestPlanId(testPlanId)
                if (versions.isEmpty()) {
                    call.respond(HttpStatusCode.NotFound, "No versions found for Test Plan ID $testPlanId")
                    return@get
                }
                call.respond(versions)
            }

            /**
             * GET /test-plan-versions/latest-by-test-plan/{testPlanId}
             * Retrieves the latest version for a specific test plan.
             */
            get("/latest-by-test-plan/{testPlanId}") {
                val testPlanId = call.parameters["testPlanId"]?.toIntOrNull()
                if (testPlanId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@get
                }
                val latestVersion = testPlanVersionService.getLatestTestPlanVersionByTestPlanId(testPlanId)
                if (latestVersion == null) {
                    call.respond(HttpStatusCode.NotFound, "No latest version found for Test Plan ID $testPlanId")
                    return@get
                }
                call.respond(latestVersion)
            }
        }
    }
}