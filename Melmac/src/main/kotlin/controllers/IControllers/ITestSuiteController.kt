package controllers.IControllers

import io.ktor.server.routing.*

interface ITestSuiteController {
    fun Route.routes()
}