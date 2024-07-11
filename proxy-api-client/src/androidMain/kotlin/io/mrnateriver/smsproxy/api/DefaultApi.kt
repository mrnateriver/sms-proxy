package io.mrnateriver.smsproxy.api

import io.mrnateriver.smsproxy.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import io.mrnateriver.smsproxy.models.MessagesProxy400Response
import io.mrnateriver.smsproxy.models.MessagesProxy422Response
import io.mrnateriver.smsproxy.models.ProxyApiInternalServerError
import io.mrnateriver.smsproxy.models.ReceiverTransientParams
import io.mrnateriver.smsproxy.models.RegisterReceiverRequest

interface DefaultApi {
    /**
     * 
     * Proxy a message, recording it the intermediary system and immediately forwarding it to the intended receiver. For security reasons, doesn&#39;t expose if the receiver is not registered.
     * Responses:
     *  - 201: The request has succeeded and a new resource has been created as a result.
     *  - 400: The server could not understand the request due to invalid syntax.
     *  - 401: Access is unauthorized.
     *  - 422: Client error
     *  - 500: Server error
     *  - 503: Service unavailable.
     *
     * @param messageProxyRequest 
     * @return [MessageProxyResponse]
     */
    @POST("messages/proxy")
    suspend fun messagesProxy(@Body messageProxyRequest: MessageProxyRequest): Response<MessageProxyResponse>

    /**
     * 
     * Register a receiver in the intermediary system. If a receiver with the specified key is already registered, the request is silently ignored for security reasons.
     * Responses:
     *  - 200: The request has succeeded.
     *  - 400: The server could not understand the request due to invalid syntax.
     *  - 401: Access is unauthorized.
     *  - 422: Client error
     *  - 500: Server error
     *  - 503: Service unavailable.
     *
     * @param registerReceiverRequest 
     * @return [Unit]
     */
    @POST("receivers/register")
    suspend fun receiversRegister(@Body registerReceiverRequest: RegisterReceiverRequest): Response<Unit>

    /**
     * 
     * Updates transient parameters of the specified receiver. 
     * Responses:
     *  - 200: The request has succeeded.
     *  - 400: The server could not understand the request due to invalid syntax.
     *  - 401: Access is unauthorized.
     *  - 422: Client error
     *  - 500: Server error
     *  - 503: Service unavailable.
     *
     * @param receiverKey 
     * @param receiverTransientParams 
     * @return [Unit]
     */
    @PATCH("receivers/{receiverKey}")
    suspend fun receiversUpdate(@Path("receiverKey") receiverKey: kotlin.String, @Body receiverTransientParams: ReceiverTransientParams): Response<Unit>

}
