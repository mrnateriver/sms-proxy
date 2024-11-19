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
import io.ktor.server.routing.patch
import io.ktor.server.util.getOrFail
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import io.mrnateriver.smsproxy.models.ReceiverTransientParams
import kotlin.Any
import kotlin.IllegalStateException
import kotlin.String

public interface ReceiversController {
  /**
   * Updates transient parameters of the specified receiver. 
   *
   * Route is expected to respond with status 200.
   * Use [io.ktor.server.response.respond] to send the response.
   *
   * @param receiverTransientParams 
   * @param receiverKey 
   * @param call The Ktor application call
   */
  public suspend fun receiversUpdate(
    receiverKey: String,
    receiverTransientParams: ReceiverTransientParams,
    principal: Principal,
    call: ApplicationCall,
  )

  public companion object {
    /**
     * Mounts all routes for the Receivers resource
     *
     * - PATCH /receivers/{receiverKey} Updates transient parameters of the specified receiver. 
     */
    public fun Route.receiversRoutes(controller: ReceiversController) {
      authenticate("BearerAuth", optional = false) {
        patch("/receivers/{receiverKey}") {
          val principal = call.principal<Principal>() ?: throw
              IllegalStateException("Principal not found")
          val receiverKey = call.parameters.getOrFail<kotlin.String>("receiverKey")
          val receiverTransientParams = call.receive<ReceiverTransientParams>()
          controller.receiversUpdate(receiverKey, receiverTransientParams, principal, call)
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
