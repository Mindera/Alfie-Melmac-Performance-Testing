package controllers

import controllers.IControllers.IMetricController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import services.IServices.IMetricService

class MetricController(
    private val metricService: IMetricService
) : IMetricController {

    override fun Route.routes() {

        route("/metrics") {

            // Listar todas as métricas
            get {
                call.respond(HttpStatusCode.OK, metricService.getAllMetrics())
            }

            // Obter uma métrica específica
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

            // Obter parâmetros da métrica (MetricParameter)
            get("/{id}/parameters") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid metric ID.")
                    return@get
                }

                val parameters = metricService.getParametersByMetricId(id)
                call.respond(HttpStatusCode.OK, parameters)
            }

            // Obter outputs
            get("/{id}/outputs") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid metric ID.")
                    return@get
                }

                val outputs = metricService.getOutputsByMetricId(id)
                call.respond(HttpStatusCode.OK, outputs)
            }

            // Obter os tipos de execução associados à métrica
            get("/{id}/execution-types") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid metric ID.")
                    return@get
                }

                val executionTypes = metricService.getExecutionTypesByMetricId(id)
                call.respond(HttpStatusCode.OK, executionTypes)
            }
        }

        // Obter parâmetros associados a um tipo de execução
        get("/execution-types/{id}/parameters") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid execution type ID.")
                return@get
            }

            val parameters = metricService.getParametersByExecutionTypeId(id)
            call.respond(HttpStatusCode.OK, parameters)
        }
    }
}
