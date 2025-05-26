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

class TestSuiteController(
    private val testSuiteService: ITestSuiteService,
    private val testExecutionService: ITestExecutionService
) : ITestSuiteController {

    override fun Route.routes() {

        route("/test-suites") {

/*             // Endpoint para adicionar um novo TestPlan à TestSuite e criar uma nova versão
            post("/{suiteId}/add-plan/{planId}") {
                val suiteId = call.parameters["suiteId"]?.toIntOrNull()
                val planId = call.parameters["planId"]?.toIntOrNull()
                if (suiteId == null || planId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid TestSuite or TestPlan ID")
                    return@post
                }

                val updatedTestSuite = testSuiteService.addTestPlanToSuite(suiteId, planId)
                call.respond(HttpStatusCode.Created, updatedTestSuite)
            } */

            // Endpoint para obter todas as suites de teste
            get {
                val testSuites = testSuiteService.getAllTestSuites()
                call.respond(testSuites)
            }

            // Endpoint para obter uma suite de teste específica
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

            // Endpoint para criar uma nova suite de teste
            post {
                val testSuiteRequest = call.receive<TestSuiteRequestDTO>()
                val createdSuite = testSuiteService.createTestSuite(testSuiteRequest)
                call.respond(HttpStatusCode.Created, createdSuite)
            }

            // Endpoint para executar todos os testes na suite (and create TestSuiteExecution)
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

            /* patch("/{id}") {
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
            } */
        }
    }
}
