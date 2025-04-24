package controllers.IControllers

import io.ktor.server.routing.*

interface IDeviceController {
    fun Route.routes()
}