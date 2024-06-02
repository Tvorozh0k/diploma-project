package com.example.mymaptestapp.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.example.mymaptestapp.R
import com.example.mymaptestapp.adapters.AdapterRouteInfo
import com.example.mymaptestapp.adapters.models.RouteInfoItem
import com.example.mymaptestapp.adapters.models.RouteInfoItemType
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.api.utils.UserRouteUtils
import com.example.mymaptestapp.databinding.ActivityRouteInfoBinding
import com.example.mymaptestapp.utils.JwtTokenUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RouteInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteInfoBinding
    private var routeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        checkUser()

        routeId = intent.getIntExtra("routeId", 0)

        initSolution()
        initToolsButton()

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.mapButton.setOnClickListener {
            val intent = Intent(this, MapRouteActivity::class.java)
            intent.putExtra("routeId", routeId)
            startActivity(intent)
        }
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
     * Проверяем JWT-токены на срок истечения
     */
    private fun checkUser() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = JwtTokenUtils.checkTokens(this@RouteInfoActivity)

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@RouteInfoActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun initSolution() {
        binding.routeInfoList.setHasFixedSize(true)
        binding.routeInfoList.setItemViewCacheSize(10)

        binding.routeId.text = String.format("# %d", routeId)

        val points = UserRouteUtils.getRoutes().find { userRoute -> userRoute.id == routeId }!!.routePoints
        val mapPoints = DeliveryPointUtils.mapPoints()

        val adapter = AdapterRouteInfo(
            this@RouteInfoActivity,
            List(points.size) { i ->
                RouteInfoItem(
                    if (i == 0) RouteInfoItemType.START_ITEM else (if (i < points.size - 1) RouteInfoItemType.LIST_ITEM else RouteInfoItemType.FINAL_ITEM),
                i + 1,
                    mapPoints.getValue(points[i])
            )
        })

        binding.routeInfoList.adapter = adapter
    }

    private fun initToolsButton() {
        val toolsButton = binding.toolsButton

        toolsButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            menuInflater.inflate(R.menu.tools_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(::manageItemClick)

            popupMenu.show()
        }
    }

    private fun manageItemClick(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteButton -> {

                val deleteDialog = AlertDialog.Builder(this, R.style.LogoutDialogTheme)
                    .setTitle("Удаление")
                    .setMessage("Вы уверены, что хотите удалить данный маршрут?")

                deleteDialog.setPositiveButton("Удалить") { dialog, which ->
                    CoroutineScope(Dispatchers.IO).launch {
                        UserRouteUtils.removeRoute(this@RouteInfoActivity, routeId)

                        withContext(Dispatchers.Main) {
                            startActivity(Intent(this@RouteInfoActivity, HomeActivity::class.java))
                        }
                    }

                    dialog.dismiss()
                }

                deleteDialog.setNegativeButton("Отмена") { dialog, id -> dialog.cancel() }

                deleteDialog.create()
                deleteDialog.show()

                true
            }
            else -> false
        }
    }
}