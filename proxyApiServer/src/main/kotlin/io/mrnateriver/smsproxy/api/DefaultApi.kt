package io.mrnateriver.smsproxy.api;

import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import io.mrnateriver.smsproxy.models.MessagesProxy400Response
import io.mrnateriver.smsproxy.models.MessagesProxy422Response
import io.mrnateriver.smsproxy.models.ProxyApiInternalServerError
import io.mrnateriver.smsproxy.models.ReceiverTransientParams
import io.mrnateriver.smsproxy.models.RegisterReceiverRequest

import javax.ws.rs.*
import javax.ws.rs.core.Response


import java.io.InputStream



@Path("/")
@javax.annotation.Generated(value = arrayOf("org.openapitools.codegen.languages.KotlinServerCodegen"), comments = "Generator version: 7.7.0")
interface DefaultApi {

    @POST
    @Path("/messages/proxy")
    @Consumes("application/json")
    @Produces("application/json")
    suspend fun messagesProxy( messageProxyRequest: MessageProxyRequest): MessageProxyResponse

    @POST
    @Path("/receivers/register")
    @Consumes("application/json")
    @Produces("application/json")
    suspend fun receiversRegister( registerReceiverRequest: RegisterReceiverRequest)

    @PATCH
    @Path("/receivers/{receiverKey}")
    @Consumes("application/json")
    @Produces("application/json")
    suspend fun receiversUpdate(@PathParam("receiverKey") receiverKey: kotlin.String, receiverTransientParams: ReceiverTransientParams)
}
