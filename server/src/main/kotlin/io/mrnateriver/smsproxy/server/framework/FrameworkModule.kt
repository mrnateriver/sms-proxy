package io.mrnateriver.smsproxy.server.framework

import dagger.BindsInstance
import dagger.Component
import dagger.Module
import io.ktor.util.logging.Logger
import io.mrnateriver.smsproxy.server.usecases.UsecasesModule
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(includes = [UsecasesModule::class])
interface FrameworkModule

@Singleton
@Component(modules = [FrameworkModule::class])
interface Framework {
    fun messagesProxyController(): MessagesProxyController
    fun receiversController(): ReceiversController

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun logger(logger: Logger): Builder

        @BindsInstance
        fun hashingSecret(@HashingSecret secret: String): Builder

        fun build(): Framework
    }
}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class HashingSecret
