package io.mrnateriver.smsproxy.server.framework

import dagger.Component
import dagger.Module
import io.mrnateriver.smsproxy.server.usecases.UsecasesModule
import javax.inject.Singleton

@Module(includes = [UsecasesModule::class])
interface FrameworkModule

@Singleton
@Component(modules = [FrameworkModule::class])
interface Framework {
    fun messagesProxyController(): MessagesProxyController
    fun receiversController(): ReceiversController
}
