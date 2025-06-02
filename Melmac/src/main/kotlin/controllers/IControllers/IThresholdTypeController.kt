package controllers.IControllers

import io.ktor.server.routing.Route

/**
 * Interface for threshold type controllers that define routing logic.
 */
interface IThresholdTypeController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}