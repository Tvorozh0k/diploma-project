package com.example.mymaptestapp.utils

import android.content.Context
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.AuthAPI
import com.example.mymaptestapp.data.JwtToken
import retrofit2.Retrofit


class JwtTokenUtils {
    companion object {
        private val PREFERENCE_NAME = "tokens"
        private val ACCESS_TOKEN = "accessToken"
        private val REFRESH_TOKEN = "refreshToken"

        fun saveTokens(context: Context, accessToken: String?, refreshToken: String?) {
            val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

            val editor = preferences.edit()
            editor.putString(ACCESS_TOKEN, accessToken)
            editor.putString(REFRESH_TOKEN, refreshToken)
            editor.apply()
        }

        fun getAccessToken(context: Context): String? {
            val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            return preferences.getString(ACCESS_TOKEN, null)
        }

        fun getRefreshToken(context: Context): String? {
            val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            return preferences.getString(REFRESH_TOKEN, null)
        }

        suspend fun checkTokens(context: Context): Boolean {
            val accessToken = getAccessToken(context)
            val refreshToken = getRefreshToken(context)

            if (accessToken != null && refreshToken != null) {
                if (!JwtToken.isExpired(JwtToken.fromToken(accessToken))) {
                    return true
                }

                if (!JwtToken.isExpired(JwtToken.fromToken(refreshToken))) {
                    refreshTokens(context)
                    return true
                }

                clearTokens(context)
            }

            return false
        }

        private suspend fun refreshTokens(context: Context) {
            val refreshToken = getRefreshToken(context)

            val retrofit = Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_ip) + "/auth/")
                .build()

            val authService = retrofit.create(AuthAPI::class.java)

            val response = authService.refreshRequest("Bearer ".plus(refreshToken))

            if (response.code() == 200) {
                val responseBody = JSONSerializer.responseBodyToMap(response.body()!!)

                saveTokens(
                    context, responseBody["accessToken"].toString(),
                    responseBody["refreshToken"].toString()
                )
            }
        }

        fun clearTokens(context: Context) {
            val preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

            val editor = preferences.edit()
            editor.remove(ACCESS_TOKEN)
            editor.remove(REFRESH_TOKEN)
            editor.apply()
        }
    }
}