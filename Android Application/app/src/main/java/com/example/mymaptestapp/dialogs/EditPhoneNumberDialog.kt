package com.example.mymaptestapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.UserAPI
import com.example.mymaptestapp.databinding.DialogEditPhoneNumberBinding
import com.example.mymaptestapp.utils.JSONSerializer
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.UserDataUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class EditPhoneNumberDialog(context: Context, private val onSave: () -> Unit) : Dialog(context) {

    private lateinit var binding: DialogEditPhoneNumberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogEditPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPhoneInput()

        binding.cancelButton.setOnClickListener {
            cancel()
        }

        binding.submitButton.setOnClickListener {
            val phoneNumber = "7 ".plus(binding.phoneEt.text)

            if (phoneNumber.length < 15) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Введите корректные данные"
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl(context?.getString(R.string.server_ip) + "/users/")
                .build()

            val userService = retrofit.create(UserAPI::class.java)

            val accessToken = JwtTokenUtils.getAccessToken(context)

            val requestBody = mapOf(
                "phoneNumber" to phoneNumber
            )

            CoroutineScope(Dispatchers.IO).launch {
                val response = userService.updateUser(
                    UserDataUtils.getUserData()!!.user.id,
                    "Bearer ".plus(accessToken),
                    JSONSerializer.mapToRequestBody(requestBody)
                )

                if (response.code() == 200) {
                    UserDataUtils.setUserData(this@EditPhoneNumberDialog.context)
                }

                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        onSave()
                        dismiss()
                    } else {
                        if (response.errorBody() == null) {
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
     * Настраиваем поле ввода номера телефона
     */
    private fun initPhoneInput() {
        // Маска для ввода номера телефона
        val slots = UnderscoreDigitSlotsParser().parseSlots("___ ___ __ __")
        val mask: MaskImpl = MaskImpl.createTerminated(slots)

        // Встраиваем маску
        val formatWatcher: FormatWatcher = MaskFormatWatcher(mask)
        formatWatcher.installOn(binding.phoneEt)
    }
}