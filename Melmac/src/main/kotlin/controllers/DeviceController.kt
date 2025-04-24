package controllers

import controllers.IControllers.IDeviceController
import domain.dtos.DeviceResponseDTO
import domain.dtos.OSResponseDTO
import domain.dtos.OSVersionResponseDTO
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*
import org.koin.ktor.ext.inject
import services.IServices.IDeviceService

class DeviceController : IDeviceController {
    override fun Route.routes() {
        val service by inject<IDeviceService>()

        route("/devices") {
            get {
                val devices: List<DeviceResponseDTO> = service.getAllDevices()
                call.respond(devices)
            }
        }

        route("/os-versions") {
            get {
                val osVersions: List<OSVersionResponseDTO> = service.getAllOSVersions()
                call.respond(osVersions)
            }
        }

        route("/oses") {
            get {
                val osList: List<OSResponseDTO> = service.getAllOS()
                call.respond(osList)
            }
        }
    }
}
