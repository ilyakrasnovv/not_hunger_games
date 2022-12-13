package hunger.hunger.web

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.routingConfiguration() {
    routing {
        get("/hello") {
            call.respondText("World!")
        }
    }
}