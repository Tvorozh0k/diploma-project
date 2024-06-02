package com.example.mymaptestapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.AccountAPI
import com.example.mymaptestapp.databinding.DialogCheckPasswordBinding
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.UserDataUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class CheckPasswordDialog(context: Context, private val editPassword: () -> Unit) : Dialog(context) {

    private lateinit var binding: DialogCheckPasswordBinding
    private var isPasswordShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogCheckPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPasswordInput(binding.passwordEt, binding.passwordButton)

        binding.cancelButton.setOnClickListener {
            cancel()
        }

        binding.submitButton.setOnClickListener {
            val password = binding.passwordEt.text.toString()

            if (password.isEmpty()) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Введите пароль"
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/accounts/")
                .build()

            val accountService = retrofit.create(AccountAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val requestBody = mapOf(
                "password" to password
            )

            CoroutineScope(Dispatchers.IO).launch {
                val response = accountService.checkPassword(
                    UserDataUtils.getUserData()!!.account.id,
                    "Bearer ".plus(accessToken),
                    JSONSerializer.mapToRequestBody(requestBody)
                )

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        dismiss()
                        editPassword()
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

    /**
     * Настраиваем поле ввода пароля
     */
    private fun initPasswordInput(editText: EditText, imageButton: ImageButton) {
        // Максимальная длина пароля
        editText.filters = arrayOf(InputFilter.LengthFilter(20))

        // Различные иконки
        val icons = arrayOf(
            R.drawable.icon_grey_hide_password,
            R.drawable.icon_grey_show_password,
            R.drawable.icon_orange_hide_password,
            R.drawable.icon_orange_show_password
        )

        // Раскрываем / скрываем пароль
        imageButton.setOnClickListener {
            isPasswordShown = !isPasswordShown

            if (isPasswordShown) {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            val position = isPasswordShown.compareTo(false) + 2 * editText.hasFocus().compareTo(false)
            imageButton.setImageResource(icons[position])
            editText.setSelection(editText.length())
        }

        // Настраиваем цвет иконки
        editText.setOnFocusChangeListener { v, hasFocus ->
            val position = isPasswordShown.compareTo(false) + 2 * hasFocus.compareTo(false)
            imageButton.setImageResource(icons[position])
        }
    }
}