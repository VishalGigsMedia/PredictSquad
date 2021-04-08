package com.predict_squad.dagger


import com.predict_squad.common_helper.Application
import com.predict_squad.retrofit.APIService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Singleton
    @Provides
    fun provideApplication() = app

    @Singleton
    @Provides
    fun provideApiInterface(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }

}