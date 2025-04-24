package controllers.IControllers

import io.ktor.server.routing.Route

interface IMetricController {
    fun Route.routes()
}
