package controllers.IControllers

import io.ktor.server.routing.Route

interface IThresholdTypeController {
    fun Route.routes()
}