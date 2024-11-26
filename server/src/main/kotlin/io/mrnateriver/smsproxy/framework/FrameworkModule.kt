package io.mrnateriver.smsproxy.framework

import dagger.Component
import dagger.Module
import io.mrnateriver.smsproxy.usecases.UsecasesModule

@Module(includes = [UsecasesModule::class])
interface FrameworkModule

@Component(modules = [FrameworkModule::class])
interface Framework {
    fun messagesProxyController(): MessagesProxyController
    fun receiversController(): ReceiversController
}
