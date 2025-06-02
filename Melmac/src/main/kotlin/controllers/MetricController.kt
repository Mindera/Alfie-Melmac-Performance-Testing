package controllers

import controllers.IControllers.IMetricController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import services.IServices.IMetricService

/**
 * Controller for handling metric-related endpoints.
 * Provides routes for retrieving metrics, metric parameters, outputs, and execution types.
 *
 * @property metricService The service used to interact with metric data sources.
 */
class MetricController(
    private val metricService: IMetricService
) : IMetricController {

    /**
     * Defines the routes for metric-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/metrics") {

            /**
             * GET /metrics
             * Retrieves all metrics.
             */
            get {
                call.respond(HttpStatusCode.OK, metricService.getAllMetrics())
            }

            /**
             * GET /metrics/{id}
             * Retrieves a specific metric by its ID.
             */
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

            /**
             * GET /metrics/{id}/parameters
             * Retrieves the parameters for a specific metric.
             */
            get("/{id}/parameters") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid metric ID.")
                    return@get
                }

                val parameters = metricService.getParametersByMetricId(id)
                call.respond(HttpStatusCode.OK, parameters)
            }

            /**
             * GET /metrics/{id}/outputs
             * Retrieves the outputs for a specific metric.
             */
            get("/{id}/outputs") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid metric ID.")
                    return@get
                }

                val outputs = metricService.getOutputsByMetricId(id)
                call.respond(HttpStatusCode.OK, outputs)
            }

            /**
             * GET /metrics/{id}/execution-types
             * Retrieves the execution types associated with a specific metric.
             */
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

        /**
         * GET /metrics/execution-types/{id}/parameters
         * Retrieves the parameters associated with a specific execution type.
         */
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