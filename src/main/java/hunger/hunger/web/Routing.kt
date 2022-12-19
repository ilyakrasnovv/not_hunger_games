package hunger.hunger.web

import hunger.hunger.Hunger
import hunger.hunger.models.Executor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.routingConfiguration() {
    routing {
        get("/update") {
            if (call.parameters["token"] != Hunger.hConfig.THIS_SERVER_ACCESS_TOKEN) {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            Hunger.state.update { }
            call.respond(HttpStatusCode.OK)
        }

        post("/execute") {
            if (call.parameters["token"] != Hunger.hConfig.THIS_SERVER_ACCESS_TOKEN) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val task = call.receive<Executor.Companion.Task>()
            try {
                Executor.execute(task)
            } catch (e: Throwable) {
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}