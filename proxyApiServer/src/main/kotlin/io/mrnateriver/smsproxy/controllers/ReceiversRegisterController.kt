package io.mrnateriver.smsproxy.controllers

import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
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
import io.mrnateriver.smsproxy.models.RegisterReceiverRequest
import kotlin.Any
import kotlin.IllegalStateException
import kotlin.String

public interface ReceiversRegisterController {
  /**
   * Register a receiver in the intermediary system. If a receiver with the specified key is already
   * registered, the request is silently ignored for security reasons.
   *
   * Route is expected to respond with status 200.
   * Use [io.ktor.server.response.respond] to send the response.
   *
   * @param registerReceiverRequest 
   * @param call The Ktor application call
   */
  public suspend fun receiversRegister(
    registerReceiverRequest: RegisterReceiverRequest,
    principal: Principal,
    call: ApplicationCall,
  )

  public companion object {
    /**
     * Mounts all routes for the ReceiversRegister resource
     *
     * - POST /receivers/register Register a receiver in the intermediary system. If a receiver with
     * the specified key is already registered, the request is silently ignored for security reasons.
     */
    public fun Route.receiversRegisterRoutes(controller: ReceiversRegisterController) {
      authenticate("BearerAuth", optional = false) {
        post("/receivers/register") {
          val principal = call.principal<Principal>() ?: throw
              IllegalStateException("Principal not found")
          val registerReceiverRequest = call.receive<RegisterReceiverRequest>()
          controller.receiversRegister(registerReceiverRequest, principal, call)
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
