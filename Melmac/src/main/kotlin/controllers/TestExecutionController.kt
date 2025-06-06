package controllers

import controllers.IControllers.ITestExecutionController
import dtos.TestExecutionResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import services.IServices.ITestExecutionService
import services.IServices.ITestMetricOutputResultService

/**
 * Controller for handling test execution-related endpoints.
 * Provides routes for retrieving and running test executions and fetching output results.
 *
 * @property testExecutionService The service used to manage test executions.
 * @property testMetricOutputResultService The service used to manage test metric output results.
 */
class TestExecutionController(
    private val testExecutionService: ITestExecutionService,
    private val testMetricOutputResultService: ITestMetricOutputResultService
) : ITestExecutionController {

    /**
     * Defines the routes for test execution-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/test-executions") {

            /**
             * GET /test-executions
             * Retrieves all test executions.
             */
            get {
                val testExecutions: List<TestExecutionResponseDTO> = testExecutionService.getAllTestExecutions()
                call.respond(testExecutions)
            }

            /**
             * GET /test-executions/{id}
             * Retrieves a specific test execution by its ID.
             */
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Execution ID")
                    return@get
                }

                val testExecution: TestExecutionResponseDTO? = testExecutionService.getTestExecutionById(id)
                if (testExecution == null) {
                    call.respond(HttpStatusCode.NotFound, "Test Execution not found")
                    return@get
                }

                call.respond(testExecution)
            }

            /**
             * POST /test-executions/run
             * Starts the execution of a test plan version.
             */
            post("/run") {
                val testPlanVersionId = call.request.queryParameters["testPlanVersionId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing or invalid testPlanVersionId")
            
                val executionResult = testExecutionService.runTestExecution(testPlanVersionId)
                call.respond(HttpStatusCode.OK, executionResult)
            }

            /**
             * GET /test-executions/outputs
             * Retrieves the output results for a specific test execution.
             */
            get("/outputs") {
                val testExecutionId = call.request.queryParameters["testExecutionId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid testExecutionId")
                
                val outputs = testMetricOutputResultService.getByExecutionId(testExecutionId)
                call.respond(outputs)
            }
        }
    }
}