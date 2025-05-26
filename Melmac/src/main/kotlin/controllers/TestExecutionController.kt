package controllers

import controllers.IControllers.ITestExecutionController
import dtos.TestExecutionResponseDTO
import dtos.TestExecutionRequestDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import services.IServices.ITestExecutionService
import services.IServices.ITestMetricOutputResultService

class TestExecutionController(
    private val testExecutionService: ITestExecutionService,
    private val testMetricOutputResultService: ITestMetricOutputResultService
) : ITestExecutionController {

    override fun Route.routes() {

        route("/test-executions") {

            // Endpoint para obter todas as execuções de teste
            get {
                val testExecutions: List<TestExecutionResponseDTO> = testExecutionService.getAllTestExecutions()
                call.respond(testExecutions)
            }

            // Endpoint para obter uma execução de teste específica
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

            // Endpoint para iniciar a execução de um teste
            post("/run") {
                val testPlanVersionId = call.request.queryParameters["testPlanVersionId"]?.toIntOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing or invalid testPlanVersionId")
            
                val executionResult = testExecutionService.runTestExecution(testPlanVersionId)
                call.respond(HttpStatusCode.OK, executionResult)
            }

            get("/outputs") {
                val testExecutionId = call.request.queryParameters["testExecutionId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing or invalid testExecutionId")
                
                val outputs = testMetricOutputResultService.getByExecutionId(testExecutionId)
                call.respond(outputs)
            }
        }
    }
}
