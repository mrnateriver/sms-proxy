package io.mrnateriver.smsproxy.controllers

import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.server.application.call
import io.ktor.server.auth.Principal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import kotlin.Any
import kotlin.IllegalStateException
import kotlin.String

public interface MessagesProxyController {
  /**
   * Proxy a message, recording it the intermediary system and immediately forwarding it to the
   * intended receiver. For security reasons, doesn't expose if the receiver is not registered.
   *
   * Route is expected to respond with [io.mrnateriver.smsproxy.models.MessageProxyResponse].
   * Use [io.mrnateriver.smsproxy.controllers.TypedApplicationCall.respondTyped] to send the
   * response.
   *
   * @param messageProxyRequest 
   * @param call Decorated ApplicationCall with additional typed respond methods
   */
  public suspend fun messagesProxy(
    messageProxyRequest: MessageProxyRequest,
    principal: Principal,
    call: TypedApplicationCall<MessageProxyResponse>,
  )

  public companion object {
    /**
     * Mounts all routes for the MessagesProxy resource
     *
     * - POST /messages/proxy Proxy a message, recording it the intermediary system and immediately
     * forwarding it to the intended receiver. For security reasons, doesn't expose if the receiver is
     * not registered.
     */
    public fun Route.messagesProxyRoutes(controller: MessagesProxyController) {
      authenticate("BearerAuth", optional = false) {
        post("/messages/proxy") {
          val principal = call.principal<Principal>() ?: throw
              IllegalStateException("Principal not found")
          val messageProxyRequest = call.receive<MessageProxyRequest>()
          controller.messagesProxy(messageProxyRequest, principal, TypedApplicationCall(call))
        }
      }
    }

    /**
     * Gets parameter value associated with this name or null if the name is not present.
     * Converting to type R using DefaultConversionService.
     *
     * Throws:
     *   ParameterConversionException - when conversion from String to R fails
     */
    private inline fun <reified R : Any> Parameters.getTyped(name: String): R? {
      val values = getAll(name) ?: return null
      val typeInfo = typeInfo<R>()
      return try {
          @Suppress("UNCHECKED_CAST")
          DefaultConversionService.fromValues(values, typeInfo) as R
      } catch (cause: Exception) {
          throw ParameterConversionException(name, typeInfo.type.simpleName ?:
          typeInfo.type.toString(), cause)
      }
    }

    /**
     * Gets first value from the list of values associated with a name.
     *
     * Throws:
     *   BadRequestException - when the name is not present
     */
    private fun Headers.getOrFail(name: String): String = this[name] ?: throw
        BadRequestException("Header " + name + " is required")
  }
}
