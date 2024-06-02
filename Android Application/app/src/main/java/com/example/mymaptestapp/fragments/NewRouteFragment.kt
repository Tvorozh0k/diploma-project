package com.example.mymaptestapp.fragments

import com.example.mymaptestapp.R
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.example.mymaptestapp.activities.ChooseStartPointActivity
import com.example.mymaptestapp.activities.LoginActivity
import com.example.mymaptestapp.databinding.FragmentNewRouteBinding
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.example.mymaptestapp.api.utils.NewRouteUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewRouteFragment : Fragment() {

    private lateinit var binding: FragmentNewRouteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()
        initRadioButtons()
        initDefaultValue()

        binding.startButton.setOnClickListener {
            startActivity(
                Intent(
                    this@NewRouteFragment.requireContext(),
                    ChooseStartPointActivity::class.java
                )
            )
        }
    }

    /**
     * Проверяем JWT-токены на срок истечения
     */
    private fun checkUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = JwtTokenUtils.checkTokens(this@NewRouteFragment.requireContext())

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(
                        Intent(
                            this@NewRouteFragment.requireContext(),
                            LoginActivity::class.java
                        )
                    )
                }
            }
        }
    }

    /**
     * Инициализация кнопок выбора опции построения маршрута
     */
    private fun initRadioButtons() {
        val buttons = arrayOf(
            binding.bruteForceButton,
            binding.antAlgorithmButton,
            binding.antOptAlgorithmButton,
            binding.autoOptionButton
        )

        val warnings = arrayOf(
            getText(R.string.brute_force_algorithm),
            getText(R.string.ant_algorithm),
            getText(R.string.ant_opt_algorithm),
            getText(R.string.auto_option)
        )

        val values = listOf(
            "held_karp",
            "aco_algorithm",
            "aco_opt_algorithm",
            "auto"
        )

        for (i in 0..3) {
            buttons[i].setOnClickListener {
                binding.chosenOption.text = HtmlCompat.fromHtml("${getString(R.string.chosen_option)} <b>${buttons[i].text}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.warningMessage.text = warnings[i]

                for (button in buttons) {
                    button.typeface = Typeface.DEFAULT
                }

                buttons[i].typeface = Typeface.DEFAULT_BOLD
                NewRouteUtils.setOption(values[i])
            }
        }
    }

    private fun initDefaultValue() {
        binding.autoOptionButton.isChecked = true
        binding.chosenOption.text = Html.fromHtml("${getString(R.string.chosen_option)} <b>${binding.autoOptionButton.text}</b>")
        binding.autoOptionButton.typeface = Typeface.DEFAULT_BOLD
        NewRouteUtils.setOption("auto")
    }
}