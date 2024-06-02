package com.example.mymaptestapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.AccountAPI
import com.example.mymaptestapp.databinding.DialogEditLoginBinding
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.UserDataUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class EditLoginDialog(context: Context, private val onSave: (String) -> Unit) : Dialog(context) {

    private lateinit var binding: DialogEditLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogEditLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelButton.setOnClickListener {
            cancel()
        }

        binding.submitButton.setOnClickListener {
            val login = binding.loginEt.text.toString().trimEnd()

            if (login.length < 3 || login.length > 50) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Введите корректные данные"
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/accounts/")
                .build()

            val accountService = retrofit.create(AccountAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val requestBody = mapOf(
                "login" to login
            )

            CoroutineScope(Dispatchers.IO).launch {
                val response = accountService.updateLogin(
                    UserDataUtils.getUserData()!!.account.id,
                    "Bearer ".plus(accessToken),
                    JSONSerializer.mapToRequestBody(requestBody)
                )

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        dismiss()
                        onSave(login)
                    } else {
                        if (response.errorBody().toString().isEmpty()) {
                            cancel()
                        } else {
                            val errorBody = JSONSerializer.responseBodyToMap(response.errorBody()!!)
                            binding.errorMessage.visibility = View.VISIBLE
                            binding.errorMessage.text = errorBody["message"].toString()
                        }
                    }
                }
            }
        }
    }
}