package com.example.mymaptestapp.api.utils

import android.content.Context
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.AccountAPI
import com.example.mymaptestapp.data.UserData
import com.example.mymaptestapp.data.entities.Account
import com.example.mymaptestapp.data.entities.User
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import retrofit2.Retrofit

class UserDataUtils {
    companion object {
        private var userData: UserData? = null

        /**
         * Подгружаем данные пользователя
         */
        suspend fun setUserData(context: Context) {
            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/accounts/")
                .build()

            val accountService = retrofit.create(AccountAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val response = accountService.getUserAccount("Bearer ".plus(accessToken))

            if (response.code() == 200) {
                val responseBody = JSONSerializer.responseBodyToMap(response.body()!!)
                userData = UserData(User.from(responseBody), Account.from(responseBody))
            }
        }

        fun getUserData(): UserData? {
            return userData
        }
    }
}