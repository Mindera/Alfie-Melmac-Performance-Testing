package core

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.context.startKoin
import config.Config

/**
 * Starts the Ktor server for the Melmac Performance Testing backend.
 *
 * This function initializes the Koin dependency injection framework and sets up
 * the Ktor server with the specified port. It includes routes for:
 * - A root endpoint (`GET /`) that returns a welcome message.
 * - A `/run-test` endpoint (`POST /run-test`) that triggers a test for a specified platform.
 *
 * @param port The port on which the server will listen.
 */
fun startServer(port: Int = Config.getServerConfig()["port"].asInt()) {
    startKoin {
        modules(appModule)
    }

    embeddedServer(Netty, port = port) {
        install(ContentNegotiation) {
            jackson()
        }
        routing {
            /**
             * Root endpoint that returns a welcome message.
             */
            get("/") {
                call.respondText("Welcome to the Melmac Performance Testing Backend!")
            }

            /**
             * Endpoint to trigger a test for a specific platform.
             *
             * Expects a JSON payload with a `platform` key, e.g.:
             * ```
             * {
             *   "platform": "ios"
             * }
             * ```
             * Responds with a success message if the platform is valid, or an error message otherwise.
             */
            post("/run-test") {
                val platform = call.receive<Map<String, String>>()["platform"]
                if (platform != null) {
                    TestRunner.run(platform)
                    call.respond(HttpStatusCode.OK, "Test for $platform started.")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Platform not specified.")
                }
            }
        }
    }.start(wait = true)
}