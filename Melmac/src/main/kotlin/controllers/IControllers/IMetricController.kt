package controllers.IControllers

import io.ktor.server.routing.Route

/**
 * Interface for metric controllers that define routing logic.
 */
interface IMetricController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}