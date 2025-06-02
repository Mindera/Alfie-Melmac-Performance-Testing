package controllers.IControllers

import io.ktor.server.routing.*

/**
 * Interface for device controllers that define routing logic.
 */
interface IDeviceController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}