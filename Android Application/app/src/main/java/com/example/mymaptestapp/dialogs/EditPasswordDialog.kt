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
import com.example.mymaptestapp.databinding.DialogEditPasswordBinding
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.UserDataUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class EditPasswordDialog(context: Context, private val restart: () -> Unit) : Dialog(context) {

    private lateinit var binding: DialogEditPasswordBinding
    private var isPasswordShown: Boolean = false
    private var isConfirmPasswordShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogEditPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPasswordInput(binding.passwordEt, binding.passwordButton, binding.confirmPasswordEt, binding.confirmPasswordButton)

        binding.cancelButton.setOnClickListener {
            cancel()
        }

        binding.submitButton.setOnClickListener {
            val password = binding.passwordEt.text.toString()
            val confirmPassword = binding.confirmPasswordEt.text.toString()

            if (password.isEmpty()) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Введите пароль"
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Повторите пароль"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Пароли не совпадают"
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
                val response = accountService.updatePassword(
                    UserDataUtils.getUserData()!!.account.id,
                    "Bearer ".plus(accessToken),
                    JSONSerializer.mapToRequestBody(requestBody)
                )

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        dismiss()
                        restart()
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
    private fun initPasswordInput(editText1: EditText, imageButton1: ImageButton, editText2: EditText, imageButton2: ImageButton) {
        // Максимальная длина пароля
        editText1.filters = arrayOf(InputFilter.LengthFilter(20))
        editText2.filters = arrayOf(InputFilter.LengthFilter(20))

        // Различные иконки
        val icons = arrayOf(
            R.drawable.icon_grey_hide_password,
            R.drawable.icon_grey_show_password,
            R.drawable.icon_orange_hide_password,
            R.drawable.icon_orange_show_password
        )

        // Раскрываем / скрываем пароль
        imageButton1.setOnClickListener {
            isPasswordShown = !isPasswordShown

            if (isPasswordShown) {
                editText1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editText1.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            val position = isPasswordShown.compareTo(false) + 2 * editText1.hasFocus().compareTo(false)
            imageButton1.setImageResource(icons[position])
            editText1.setSelection(editText1.length())
        }

        imageButton2.setOnClickListener {
            isConfirmPasswordShown = !isConfirmPasswordShown

            if (isConfirmPasswordShown) {
                editText2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editText2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            val position = isConfirmPasswordShown.compareTo(false) + 2 * editText2.hasFocus().compareTo(false)
            imageButton2.setImageResource(icons[position])
            editText2.setSelection(editText2.length())
        }

        // Настраиваем цвет иконки
        editText1.setOnFocusChangeListener { v, hasFocus ->
            val position = isPasswordShown.compareTo(false) + 2 * hasFocus.compareTo(false)
            imageButton1.setImageResource(icons[position])
        }

        editText2.setOnFocusChangeListener { v, hasFocus ->
            val position = isConfirmPasswordShown.compareTo(false) + 2 * hasFocus.compareTo(false)
            imageButton2.setImageResource(icons[position])
        }
    }
}