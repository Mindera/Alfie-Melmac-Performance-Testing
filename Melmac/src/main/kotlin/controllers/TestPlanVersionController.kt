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

class TestPlanVersionController(
    private val testPlanVersionService: ITestPlanVersionService
) : ITestPlanVersionController {

    override fun Route.routes() {
        route("/test-plan-versions") {

            // Get a specific test plan version by ID
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