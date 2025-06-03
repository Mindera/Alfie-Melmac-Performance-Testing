package controllers

import controllers.IControllers.ITestSuiteController
import dtos.TestSuiteResponseDTO
import dtos.TestSuiteRequestDTO
import dtos.TestPlanVersionResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import services.IServices.ITestSuiteService
import services.IServices.ITestExecutionService

/**
 * Controller for handling test suite-related endpoints.
 * Provides routes for retrieving, creating, and managing test suites.
 *
 * @property testSuiteService The service used to interact with test suite data sources.
 * @property testExecutionService The service used to manage test executions.
 */
class TestSuiteController(
    private val testSuiteService: ITestSuiteService,
    private val testExecutionService: ITestExecutionService
) : ITestSuiteController {

    /**
     * Defines the routes for test suite-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/test-suites") {

            /*
            // POST /test-suites/{suiteId}/add-plan/{planId}
            // Adds a new TestPlan to a TestSuite and creates a new version.
            post("/{suiteId}/add-plan/{planId}") {
                val suiteId = call.parameters["suiteId"]?.toIntOrNull()
                val planId = call.parameters["planId"]?.toIntOrNull()
                if (suiteId == null || planId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid TestSuite or TestPlan ID")
                    return@post
                }

                val updatedTestSuite = testSuiteService.addTestPlanToSuite(suiteId, planId)
                call.respond(HttpStatusCode.Created, updatedTestSuite)
            }
            */

            /**
             * GET /test-suites
             * Retrieves all test suites.
             */
            get {
                val testSuites = testSuiteService.getAllTestSuites()
                call.respond(testSuites)
            }

            /**
             * GET /test-suites/{id}
             * Retrieves a specific test suite by its ID.
             */
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Suite ID")
                    return@get
                }

                val testSuite = testSuiteService.getTestSuiteById(id)
                if (testSuite == null) {
                    call.respond(HttpStatusCode.NotFound, "Test Suite not found")
                    return@get
                }

                call.respond(testSuite)
            }

            /**
             * POST /test-suites
             * Creates a new test suite.
             */
            post {
                val testSuiteRequest = call.receive<TestSuiteRequestDTO>()
                val createdSuite = testSuiteService.createTestSuite(testSuiteRequest)
                call.respond(HttpStatusCode.Created, createdSuite)
            }

            /**
             * POST /test-suites/{id}/run
             * Executes all tests in the suite and creates a TestSuiteExecution.
             */
            post("/{id}/run") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Suite ID")
                    return@post
                }

                try {
                    val suiteExecution = testSuiteService.runTestSuiteExecution(id)
                    call.respond(HttpStatusCode.OK, suiteExecution)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, e.message ?: "Execution failed")
                }
            }

            /*
            // PATCH /test-suites/{id}
            // Updates a test suite's details.
            patch("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Suite ID")
                    return@patch
                }
            
                val updateDTO = call.receive<TestSuiteUpdateDTO>()
                try {
                    val updatedSuite = testSuiteService.updateTestSuiteDetails(id, updateDTO)
                    call.respond(HttpStatusCode.OK, updatedSuite)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Update failed")
                }
            }
            
            // PATCH /test-suites/{id}/active-status
            // Updates the active status of a test suite.
            patch("/{id}/active-status") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Suite ID")
                    return@patch
                }
            
                val isActive = call.receive<Boolean>()
                try {
                    val updatedSuite = testSuiteService.updateTestSuiteActiveStatus(id, isActive)
                    call.respond(HttpStatusCode.OK, updatedSuite)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update Test Suite status")
                }
            }
            */
        }
    }
}