package controllers.IControllers

import io.ktor.server.routing.Route

/**
 * Interface for threshold controllers that define routing logic.
 */
interface IThresholdController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}