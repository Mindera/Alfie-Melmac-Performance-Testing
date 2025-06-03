package controllers.IControllers

import io.ktor.server.routing.*

/**
 * Interface for test plan version controllers that define routing logic.
 */
interface ITestPlanVersionController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}