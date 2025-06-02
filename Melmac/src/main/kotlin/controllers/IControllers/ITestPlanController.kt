package controllers.IControllers

import io.ktor.server.routing.*

/**
 * Interface for test plan controllers that define routing logic.
 */
interface ITestPlanController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}