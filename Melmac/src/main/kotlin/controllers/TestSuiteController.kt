package controllers

import controllers.IControllers.ITestSuiteController
import services.IServices.ITestSuiteService
import domain.dtos.TestSuiteRequestDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

class TestSuiteController : ITestSuiteController {
    override fun Route.routes() {
        val service by inject<ITestSuiteService>()

        route("/test-suites") {
            post {
                val request = call.receive<TestSuiteRequestDTO>()
                if (request.name.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Missing name.")
                    return@post
                }
                val suite = service.create(request)
                call.respond(HttpStatusCode.Created, suite)
            }

            get {
                call.respond(service.listAll())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val suite = id?.let { service.findById(it) }
                if (suite == null) {
                    call.respond(HttpStatusCode.NotFound, "Suite not found.")
                } else {
                    call.respond(suite)
                }
            }
        }
    }
}
