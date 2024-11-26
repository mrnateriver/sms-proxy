package io.mrnateriver.smsproxy.usecases

import dagger.Module
import io.mrnateriver.smsproxy.data.DataModule

@Module(includes = [DataModule::class])
interface UsecasesModule {
}
