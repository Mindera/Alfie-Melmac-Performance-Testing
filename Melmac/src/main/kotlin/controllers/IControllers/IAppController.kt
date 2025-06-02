package controllers.IControllers

import io.ktor.server.routing.*

/**
 * Interface for application controllers that define routing logic.
 */
interface IAppController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}