package com.example.mymaptestapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.UserAPI
import com.example.mymaptestapp.api.utils.UserDataUtils
import com.example.mymaptestapp.databinding.DialogEditFullNameBinding
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit


class EditFullNameDialog(context: Context, private val onSave: () -> Unit) : Dialog(context) {

    private lateinit var binding: DialogEditFullNameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogEditFullNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cancelButton.setOnClickListener {
            cancel()
        }

        binding.submitButton.setOnClickListener {
            val lastName = binding.lastNameEt.text.toString().trimEnd()
            val firstName = binding.firstNameEt.text.toString().trimEnd()
            val patronymic = binding.patronymicEt.text.toString().trimEnd()

            val regex = Regex("^[А-Я][а-я]+\$")
            val regexPatronymic = Regex("(^[А-Я][а-я]+\$)|(^\$)")

            if (!regex.matches(lastName) || !regex.matches(firstName) || !regexPatronymic.matches(patronymic)) {
                binding.errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (lastName.length > 50 || firstName.length > 50 || patronymic.length > 50) {
                binding.errorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/users/")
                .build()

            val userService = retrofit.create(UserAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val requestBody = mapOf(
                "lastName" to lastName,
                "firstName" to firstName,
                "patronymic" to patronymic
            )

            CoroutineScope(Dispatchers.IO).launch {
                val response = userService.updateUser(
                    UserDataUtils.getUserData()!!.user.id,
                    "Bearer ".plus(accessToken),
                    JSONSerializer.mapToRequestBody(requestBody)
                )

                if (response.code() == 200) {
                    UserDataUtils.setUserData(this@EditFullNameDialog.context)
                }

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        onSave()
                        dismiss()
                    } else {
                        cancel()
                    }
                }
            }
        }
    }
}