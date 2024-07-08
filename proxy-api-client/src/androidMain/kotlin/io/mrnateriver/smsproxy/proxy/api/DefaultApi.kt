package io.mrnateriver.smsproxy.proxy.api

import io.mrnateriver.smsproxy.proxy.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.squareup.moshi.Json

import io.mrnateriver.smsproxy.proxy.models.MessageProxyInternalServerError
import io.mrnateriver.smsproxy.proxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.proxy.models.MessageProxyResponse
import io.mrnateriver.smsproxy.proxy.models.MessagesProxy400Response
import io.mrnateriver.smsproxy.proxy.models.MessagesProxy422Response
import io.mrnateriver.smsproxy.proxy.models.RecipientTransientParamsUpdate
import io.mrnateriver.smsproxy.proxy.models.RegisterRecipientRequest

interface DefaultApi {
    /**
     * 
     * Proxy a message, recording it the intermediary system and immediately forwarding it to the intended recipient. For security reasons, doesn&#39;t expose if the recipient is not registered.
     * Responses:
     *  - 201: The request has succeeded and a new resource has been created as a result.
     *  - 400: The server could not understand the request due to invalid syntax.
     *  - 401: Access is unauthorized.
     *  - 422: Client error
     *  - 500: Server error
     *  - 503: Service unavailable.
     *
     * @param messageProxyRequest 
     * @return [Call]<[MessageProxyResponse]>
     */
    @POST("messages/proxy")
    fun messagesProxy(@Body messageProxyRequest: MessageProxyRequest): Call<MessageProxyResponse>

    /**
     * 
     * Register a recipient in the intermediary system. If a recipient with the specified key is already registered, the request is silently ignored for security reasons.
     * Responses:
     *  - 200: The request has succeeded.
     *  - 400: The server could not understand the request due to invalid syntax.
     *  - 401: Access is unauthorized.
     *  - 422: Client error
     *  - 500: Server error
     *  - 503: Service unavailable.
     *
     * @param registerRecipientRequest 
     * @return [Call]<[Unit]>
     */
    @POST("recipients/register")
    fun recipientsRegister(@Body registerRecipientRequest: RegisterRecipientRequest): Call<Unit>

    /**
     * 
     * Updates transient parameters of the specified recipient. 
     * Responses:
     *  - 200: The request has succeeded.
     *  - 400: The server could not understand the request due to invalid syntax.
     *  - 401: Access is unauthorized.
     *  - 422: Client error
     *  - 500: Server error
     *  - 503: Service unavailable.
     *
     * @param recipientKey 
     * @param recipientTransientParamsUpdate 
     * @return [Call]<[Unit]>
     */
    @PATCH("recipients/{recipientKey}")
    fun recipientsUpdate(@Path("recipientKey") recipientKey: kotlin.String, @Body recipientTransientParamsUpdate: RecipientTransientParamsUpdate): Call<Unit>

}
