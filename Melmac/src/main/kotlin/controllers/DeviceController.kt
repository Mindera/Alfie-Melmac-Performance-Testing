package controllers

import controllers.IControllers.IDeviceController
import dtos.AvailableDeviceDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.IDeviceService

class DeviceController(private val deviceService: IDeviceService) : IDeviceController {

    override fun Route.routes() {

        route("/devices") {

            // Endpoint to get all available devices
            get {
                val platform = call.request.queryParameters["platform"]?.lowercase()
                val devices: List<AvailableDeviceDTO> = deviceService.getAllAvailableDevices()
                    .filter { platform == null || it.osName.lowercase() == platform }
                call.respond(devices)
            }

            // Endpoint to get device by ID
            get("/{deviceId}") {
                val deviceId = call.parameters["deviceId"]?.toIntOrNull()
                if (deviceId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid Device ID")
                    return@get
                }
                val device = deviceService.getDeviceById(deviceId)
                if (device != null) {
                    call.respond(device)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Device not found")
                }
            }

            // Endpoint to get devices by minimum OS version
            get("/os-minimum/{minOsVersionId}") {
                val minVersion = call.parameters["minOsVersionId"]
                val platform = call.request.queryParameters["platform"]?.lowercase()

                val devices =
                        if (minVersion != null) {
                            deviceService.getAvailableDevicesByMinVersion(minVersion).filter {
                                platform == null || it.osName.lowercase() == platform
                            }
                        } else {
                            deviceService.getAllAvailableDevices().filter {
                                platform == null || it.osName.lowercase() == platform
                            }
                        }

                call.respond(devices)
            }
        }
    }
}
