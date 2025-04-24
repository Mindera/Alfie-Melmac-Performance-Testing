package controllers

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import services.IServices.IMetricService
import controllers.IControllers.IMetricController

class MetricController : IMetricController {
    override fun Route.routes() {
        val metricService by inject<IMetricService>()

        route("/metrics") {
            get {
                call.respond(HttpStatusCode.OK, metricService.getAllMetrics())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid metric ID.")
                    return@get
                }

                val metric = metricService.getMetricById(id)
                if (metric != null) {
                    call.respond(HttpStatusCode.OK, metric)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Metric not found.")
                }
            }

            get("/{id}/outputs") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.OK, metricService.getOutputsByMetricId(id))
            }
            
            get("/{id}/execution-types") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.OK, metricService.getExecutionTypesByMetricId(id))
            }
            
            get("/{id}/parameters") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.OK, metricService.getMetricParametersByMetricId(id))
            }
        }
    }
}
