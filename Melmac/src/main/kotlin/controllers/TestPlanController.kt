package controllers

import controllers.IControllers.ITestPlanController
import dtos.TestPlanResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import dtos.TestPlanRequestDTO
import dtos.TestPlanVersionRequestDTO
import services.IServices.ITestPlanService

class TestPlanController(
    private val testPlanService: ITestPlanService
) : ITestPlanController {

    override fun Route.routes() {

        route("/test-plans") {

            // Endpoint para obter um plano de teste específico
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

            // Endpoint para criar um novo plano de teste e sua versão associada
            post {
                println("Creating a new test plan")
                val testPlanRequest = try {
                    call.receive<TestPlanVersionRequestDTO>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                    return@post
                }

                val createdPlan = try {
                    testPlanService.createTestPlanWithVersion(testPlanRequest)
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid data")
                    return@post
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Failed to create test plan", "message" to (e.message ?: "Unknown error"))
                    )
                    return@post
                }

                call.respond(HttpStatusCode.Created, createdPlan)
            }

            /* // Endpoint para criar uma nova versão de um plano de teste
            post("/{id}/version") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@post
                }

                val updatedPlanVersion = testPlanService.createNewVersionOfTestPlan(id)
                call.respond(HttpStatusCode.Created, updatedPlanVersion)
            }

            // Endpoint para excluir um plano de teste
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Test Plan ID")
                    return@delete
                }

                testPlanService.deleteTestPlan(id)
                call.respond(HttpStatusCode.NoContent)
            } */
        }
    }
}
