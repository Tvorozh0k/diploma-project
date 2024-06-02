package com.example.mymaptestapp.fragments

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.mymaptestapp.R
import com.example.mymaptestapp.activities.LoginActivity
import com.example.mymaptestapp.activities.SplashActivity
import com.example.mymaptestapp.api.utils.UserDataUtils
import com.example.mymaptestapp.databinding.FragmentAccountBinding
import com.example.mymaptestapp.dialogs.CheckPasswordDialog
import com.example.mymaptestapp.dialogs.EditFullNameDialog
import com.example.mymaptestapp.dialogs.EditLoginDialog
import com.example.mymaptestapp.dialogs.EditPasswordDialog
import com.example.mymaptestapp.dialogs.EditPhoneNumberDialog
import com.example.mymaptestapp.utils.JwtTokenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()
        initUser()

        initLogoutButton()
        initCopyText()
        initEditText()
    }

    /**
     * Проверяем JWT-токены на срок истечения
     */
    private fun checkUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = JwtTokenUtils.checkTokens(this@AccountFragment.requireContext())

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@AccountFragment.requireContext(), LoginActivity::class.java))
                }
            }
        }
    }

    /**
     * Подгружаем данные пользователя
     */
    private fun initUser() {
        val user = UserDataUtils.getUserData()?.user!!
        val account = UserDataUtils.getUserData()?.account!!

        binding.apply {
            fullNameText.text = String.format("%s %s %s", user.lastName, user.firstName, user.patronymic)
            phoneNumberText.text = String.format("+%s", user.phoneNumber)
            loginText.text = account.login
        }
    }

    /**
     * Настраиваем выход из аккаунта
     */
    private fun initLogoutButton() {
        binding.logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        binding.logoutNext.setOnClickListener {
            showLogoutDialog()
        }
    }

    /**
     * AlertDialog для выхода из аккаунта
     */
    private fun showLogoutDialog() {
        val logoutDialog = AlertDialog.Builder(this.requireContext(), R.style.LogoutDialogTheme)
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти?")

        logoutDialog.setPositiveButton("Выйти") { dialog, which ->
            JwtTokenUtils.clearTokens(this.requireContext())
            startActivity(Intent(this.requireContext(), LoginActivity::class.java))
            dialog.dismiss()
        }

        logoutDialog.setNegativeButton("Отмена") { dialog, id -> dialog.cancel() }

        logoutDialog.create()
        logoutDialog.show()
    }

    /**
     * Копируем информацию о пользователе
     */
    private fun initCopyText() {
        val clipboardManager = getSystemService(this.requireContext(), ClipboardManager::class.java)

        // Копируем ФИО
        binding.fullNameCopy.setOnClickListener {
            val clipData = ClipData.newPlainText("ФИО", binding.fullNameText.text)
            clipboardManager?.setPrimaryClip(clipData)

            Toast.makeText(this.requireContext(), "Данные были скопированы", Toast.LENGTH_SHORT).show()
        }

        // Копируем номер телефона
        binding.phoneNumberCopy.setOnClickListener {
            val clipData = ClipData.newPlainText("Номер телефона", binding.phoneNumberText.text)
            clipboardManager?.setPrimaryClip(clipData)

            Toast.makeText(this.requireContext(), "Данные были скопированы", Toast.LENGTH_SHORT).show()
        }

        // Копируем логин
        binding.loginCopy.setOnClickListener {
            val clipData = ClipData.newPlainText("Логин", binding.loginText.text)
            clipboardManager?.setPrimaryClip(clipData)

            Toast.makeText(this.requireContext(), "Данные были скопированы", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Редактируем информацию о пользователе
     */
    private fun initEditText() {
        // Редактирование ФИО
        binding.fullNameNext.setOnClickListener {
            checkUser()

            val dialog = EditFullNameDialog(this.requireContext()) {
                initUser()
            }

            dialog.show()
        }

        // Редактирование номера телефона
        binding.phoneNumberNext.setOnClickListener {
            checkUser()

            val dialog = EditPhoneNumberDialog(this.requireContext()) {
                initUser()
            }

            dialog.show()
        }

        // Редактирование логина
        binding.loginNext.setOnClickListener {
            checkUser()

            val dialog = EditLoginDialog(this.requireContext()) { login ->
                binding.loginText.text = login

                val restartDialog = AlertDialog.Builder(this@AccountFragment.requireContext())
                    .setView(R.layout.dialog_restart)
                    .setCancelable(false)
                    .create()

                restartDialog.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    restartDialog.dismiss()
                    JwtTokenUtils.clearTokens(this@AccountFragment.requireContext())
                    startActivity(Intent(this@AccountFragment.requireContext(), SplashActivity::class.java))
                }, 1000)
            }

            dialog.show()
        }

        // Редактирование пароля
        binding.passwordNext.setOnClickListener {
            checkUser()

            val dialog = CheckPasswordDialog(this.requireContext()) {
                val editPasswordDialog = EditPasswordDialog(this.requireContext()) {
                    val restartDialog = AlertDialog.Builder(this@AccountFragment.requireContext())
                        .setView(R.layout.dialog_restart)
                        .setCancelable(false)
                        .create()

                    restartDialog.show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        restartDialog.dismiss()
                        JwtTokenUtils.clearTokens(this@AccountFragment.requireContext())
                        startActivity(Intent(this@AccountFragment.requireContext(), SplashActivity::class.java))
                    }, 1000)
                }

                editPasswordDialog.show()
            }

            dialog.show()
        }
    }
}