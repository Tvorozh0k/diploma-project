package com.example.mymaptestapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.AuthAPI
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.api.utils.UserDataUtils
import com.example.mymaptestapp.api.utils.UserRouteUtils
import com.example.mymaptestapp.databinding.ActivityLoginBinding
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        initPasswordInput(binding.passwordEt, binding.passwordButton)
        initLoginButton(binding.loginButton)
    }

    /**
     * Настраиваем строку состояния
     */
    private fun initStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * Вспомогательная функция для настройки строки состояния
     */
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes

        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }

        win.attributes = winParams
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
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            val position =
                isPasswordShown.compareTo(false) + 2 * editText.hasFocus().compareTo(false)
            imageButton.setImageResource(icons[position])
            editText.setSelection(editText.length())
        }

        // Настраиваем цвет иконки
        editText.setOnFocusChangeListener { v, hasFocus ->
            val position = isPasswordShown.compareTo(false) + 2 * hasFocus.compareTo(false)
            imageButton.setImageResource(icons[position])
        }
    }

    /**
     * Настраиваем вход в аккаунт
     */
    private fun initLoginButton(button: MaterialButton) {
        button.setOnClickListener {
            val login = binding.loginEt.text.toString().trimEnd()
            val password = binding.passwordEt.text.toString()

            if (login.length < 3 || login.length > 50) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Введите корректный логин"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Введите пароль"
                return@setOnClickListener
            }

            val requestBody = mapOf(
                "login" to login,
                "password" to password
            )

            val retrofit = Retrofit.Builder()
                .baseUrl(getString(R.string.server_ip) + "/auth/")
                .build()

            val authService = retrofit.create(AuthAPI::class.java)

            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    authService.loginRequest(JSONSerializer.mapToRequestBody(requestBody))

                if (response.code() == 200) {
                    val responseBody = JSONSerializer.responseBodyToMap(response.body()!!)

                    JwtTokenUtils.saveTokens(
                        this@LoginActivity, responseBody["accessToken"].toString(),
                        responseBody["refreshToken"].toString()
                    )

                    UserDataUtils.setUserData(this@LoginActivity)
                    DeliveryPointUtils.setPoints(this@LoginActivity)
                    UserRouteUtils.setRoutes(this@LoginActivity)
                }

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
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