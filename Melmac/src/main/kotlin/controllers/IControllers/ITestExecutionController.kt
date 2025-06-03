package controllers.IControllers

import io.ktor.server.routing.*

/**
 * Interface for test execution controllers that define routing logic.
 */
interface ITestExecutionController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}