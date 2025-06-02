package controllers

import controllers.IControllers.ITestPlanController
import dtos.TestPlanResponseDTO
import dtos.TestPlanVersionRequestDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.ITestPlanService

/**
 * Controller for handling test plan-related endpoints.
 * Provides routes for retrieving, creating, and managing test plans.
 *
 * @property testPlanService The service used to interact with test plan data sources.
 */
class TestPlanController(private val testPlanService: ITestPlanService) : ITestPlanController {

    /**
     * Defines the routes for test plan-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/test-plans") {

            /**
             * GET /test-plans/{id}
             * Retrieves a specific test plan by its ID.
             */
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@get
                }

                val testPlan: TestPlanResponseDTO? = testPlanService.getTestPlanById(id)
                if (testPlan == null) {
                    call.respond(HttpStatusCode.NotFound, "Test Plan not found")
                    return@get
                }

                call.respond(testPlan)
            }

            /**
             * POST /test-plans
             * Creates a new test plan and its associated version.
             */
            post {
                println("Creating a new test plan")
                val testPlanRequest =
                        try {
                            call.receive<TestPlanVersionRequestDTO>()
                        } catch (e: Exception) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                            return@post
                        }

                val createdPlan =
                        try {
                            testPlanService.createTestPlanWithVersion(testPlanRequest)
                        } catch (e: IllegalArgumentException) {
                            call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
                            return@post
                        } catch (e: Exception) {
                            call.respond(
                                    HttpStatusCode.InternalServerError,
                                    mapOf(
                                            "error" to "Failed to create test plan",
                                            "message" to (e.message ?: "Unknown error")
                                    )
                            )
                            return@post
                        }

                call.respond(HttpStatusCode.Created, createdPlan)
            }

            /* 
            // POST /test-plans/{id}/version
            // Creates a new version of a test plan.
            post("/{id}/version") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@post
                }

                val updatedPlanVersion = testPlanService.createNewVersionOfTestPlan(id)
                call.respond(HttpStatusCode.Created, updatedPlanVersion)
            }

            // DELETE /test-plans/{id}
            // Deletes a test plan.
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@delete
                }

                testPlanService.deleteTestPlan(id)
                call.respond(HttpStatusCode.NoContent)
            } 
            */
        }
    }
}