package com.example.mymaptestapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.mymaptestapp.activities.HomeActivity
import com.example.mymaptestapp.activities.LoginActivity
import com.example.mymaptestapp.activities.RouteInfoActivity
import com.example.mymaptestapp.adapters.AdapterRouteHistory
import com.example.mymaptestapp.adapters.AdapterStartPointList
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.api.utils.UserRouteUtils
import com.example.mymaptestapp.databinding.FragmentRouteHistoryBinding
import com.example.mymaptestapp.utils.JwtTokenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteHistoryFragment : Fragment() {

    private lateinit var binding: FragmentRouteHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()
        initRoutes()
    }

    /**
     * Проверяем JWT-токены на срок истечения
     */
    private fun checkUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = JwtTokenUtils.checkTokens(this@RouteHistoryFragment.requireContext())

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@RouteHistoryFragment.requireContext(), LoginActivity::class.java))
                }
            }
        }
    }

    /**
     * Загрузка списка маршрутов пользователя
     */
    private fun initRoutes() {
        binding.routesList.setHasFixedSize(true)
        binding.routesList.setItemViewCacheSize(10)

        val adapter = AdapterRouteHistory(this@RouteHistoryFragment.requireContext(), UserRouteUtils.getRoutes()) { id ->
            val intent = Intent(this.requireContext(), RouteInfoActivity::class.java)
            intent.putExtra("routeId", id)
            startActivity(intent)
        }

        binding.routesList.adapter = adapter
    }
}