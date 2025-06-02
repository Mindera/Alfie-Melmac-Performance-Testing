package controllers.IControllers

import io.ktor.server.routing.*

/**
 * Interface for test suite controllers that define routing logic.
 */
interface ITestSuiteController {
    /**
     * Defines the routes for the controller.
     *
     * @receiver Route The Ktor routing context.
     */
    fun Route.routes()
}