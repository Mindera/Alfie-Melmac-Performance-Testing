package controllers.IControllers

import io.ktor.server.routing.*

interface IAppController {
    fun Route.routes()
}