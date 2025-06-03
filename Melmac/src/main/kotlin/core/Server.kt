package core

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import config.Config
import controllers.IControllers.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import services.IServices.*

/**
 * Main Ktor application module.
 *
 * Configures content negotiation, dependency injection, error handling, and registers all API routes.
 */
fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    install(Koin) {
        modules(appModule)
    }

    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "Bad Request")
        }
        exception<NoSuchElementException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, cause.message ?: "Not found")
        }
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Something went wrong")
        }
    }

    routing {
        get("/") {
            call.respondText("Welcome to the Melmac Performance Testing API!", ContentType.Text.Plain)
        }

        val loaderService by inject<ILoaderService>()
        loaderService.syncDataFromConfig()

        val deviceService by inject<IDeviceService>()
        deviceService.getAllAvailableDevices()

        val appController by inject<IAppController>()
        with(appController) { routes() }

        val deviceController by inject<IDeviceController>()
        with(deviceController) { routes() }

        val metricController by inject<IMetricController>()
        with(metricController) { routes() }

        val testExecutionController by inject<ITestExecutionController>()
        with(testExecutionController) { routes() }

        val testPlanController by inject<ITestPlanController>()
        with(testPlanController) { routes() }

        val testSuiteController by inject<ITestSuiteController>()
        with(testSuiteController) { routes() }

        val thresholdController by inject<IThresholdController>()
        with(thresholdController) { routes() }

        val thresholdTypeController by inject<IThresholdTypeController>()
        with(thresholdTypeController) { routes() }

        val testPlanVersionController by inject<ITestPlanVersionController>()
        with(testPlanVersionController) { routes() }
    }
}