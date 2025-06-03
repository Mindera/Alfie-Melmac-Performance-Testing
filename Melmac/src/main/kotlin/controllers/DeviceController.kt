package controllers

import controllers.IControllers.IDeviceController
import dtos.AvailableDeviceDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import services.IServices.IDeviceService

/**
 * Controller for handling device-related endpoints.
 * Provides routes for retrieving available devices and device details.
 *
 * @property deviceService The service used to interact with device data sources.
 */
class DeviceController(private val deviceService: IDeviceService) : IDeviceController {

    /**
     * Defines the routes for device-related operations.
     *
     * @receiver Route The Ktor routing context.
     */
    override fun Route.routes() {

        route("/devices") {

            /**
             * GET /devices
             * Retrieves all available devices, optionally filtered by platform.
             */
            get {
                val platform = call.request.queryParameters["platform"]?.lowercase()
                val devices: List<AvailableDeviceDTO> = deviceService.getAllAvailableDevices()
                    .filter { platform == null || it.osName.lowercase() == platform }
                call.respond(devices)
            }

            /**
             * GET /devices/{deviceId}
             * Retrieves a device by its ID.
             */
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

            /**
             * GET /devices/os-minimum/{minOsVersionId}
             * Retrieves devices with a minimum OS version, optionally filtered by platform.
             */
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