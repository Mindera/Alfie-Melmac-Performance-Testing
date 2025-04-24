package controllers.IControllers

import io.ktor.server.routing.Route

interface IThresholdController {
    fun Route.routes()
}
