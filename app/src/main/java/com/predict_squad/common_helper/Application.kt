package com.predict_squad.common_helper

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.predict_squad.dagger.AppComponent
import com.predict_squad.dagger.AppModule
import com.predict_squad.dagger.DaggerAppComponent
import com.predict_squad.dagger.RetrofitModule
import com.predict_squad.R


@SuppressLint("StaticFieldLeak")
class Application : Application() {

    companion object {
        var instance: com.predict_squad.common_helper.Application? = null
        lateinit var mComponent: AppComponent
        var appContext: Context? = null

        fun get(): com.predict_squad.common_helper.Application? {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext

        //initialize dagger
        mComponent = DaggerAppComponent.builder().appModule(AppModule(this)).retrofitModule(RetrofitModule(this, getString(R.string.server_url))).build()
        mComponent.inject(this)
    }


    public fun getComponent(): AppComponent {
        return mComponent
    }
}