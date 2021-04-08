package com.predict_squad.common_helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import okhttp3.Cookie
import java.util.*

open class PreferenceHelper(context: Context) {

    private val sharedPreferences: SharedPreferences

    init {
        val preference = ConstantHelper.preferenceName
        this.sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE)
    }


    fun setKey(key: String, value: String) {
        this.sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return this.sharedPreferences.getString(key, "").toString()
    }

    enum class Key {
        UserLoggedIn, AuthorizationKey, UserId, FcmToken

    }

    fun setJwtToken(value: String) {
        this.sharedPreferences.edit().putString(Key.AuthorizationKey.name, value).apply()
    }

    fun getJwtToken(): String {
        return this.sharedPreferences.getString(Key.AuthorizationKey.name, "").toString()
    }


    fun setLoggedIn(value: Boolean) {
        this.sharedPreferences.edit().putBoolean(Key.UserLoggedIn.name, value).apply()
    }

    fun getLoggedIn(): Boolean {
        return this.sharedPreferences.getBoolean(Key.UserLoggedIn.name, false)
    }


    fun setFcmToken(value: String) {
        this.sharedPreferences.edit().putString(Key.FcmToken.name, value).apply()
    }

    fun getFcmToken(): String {
        return this.sharedPreferences.getString(Key.FcmToken.name, "").toString()
    }

    fun setBoolean(key: String, value: Boolean) {
        this.sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return this.sharedPreferences.getBoolean(key, false)
    }

    internal fun setClear() {
        this.sharedPreferences.edit().clear().apply()
    }

    open fun setClearSpecific(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun setInt(key: String, value: Int) {
        this.sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String): Int {
        return this.sharedPreferences.getInt(key, 0)
    }

    internal fun setCookies(base_url: String, cookies: List<Cookie>) {
        // Convert cookies to JSON and store them
        val gson = Gson()
        val cookiesString = gson.toJson(cookies)
        this.sharedPreferences.edit().putString(base_url, cookiesString).apply()
    }

    internal fun getCookies(base_url: String): List<Cookie> {
        val gson = Gson()
        val cookiesString = this.sharedPreferences.getString(base_url, null)
        return if (cookiesString != null) {
            Arrays.asList(*gson.fromJson(cookiesString, Array<Cookie>::class.java))
        } else ArrayList()
    }
}