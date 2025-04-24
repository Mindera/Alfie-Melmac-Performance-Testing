package controllers

import controllers.IControllers.ITestExecutionController
import services.IServices.ITestExecutionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import domain.dtos.TestExecutionRequestDTO
import domain.dtos.TestExecutionResponseDTO

class TestExecutionController : ITestExecutionController {
    override fun Route.routes() {
        val service by inject<ITestExecutionService>()

        route("/test-executions") {

            post {
                val request = call.receive<TestExecutionRequestDTO>()
                val exec: TestExecutionResponseDTO = service.create(request)
                call.respond(HttpStatusCode.Created, exec)
            }

            get {
                val allExecutions: List<TestExecutionResponseDTO> = service.listAll()
                call.respond(HttpStatusCode.OK, allExecutions)
            }

            get("/by-suite/{suiteId}") {
                val suiteId = call.parameters["suiteId"]?.toIntOrNull()
                if (suiteId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid suite ID.")
                    return@get
                }

                val executionsBySuite: List<TestExecutionResponseDTO> = service.listBySuiteId(suiteId)
                call.respond(HttpStatusCode.OK, executionsBySuite)
            }
        }
    }
}
