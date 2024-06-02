package com.example.mymaptestapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.example.mymaptestapp.BuildConfig
import com.example.mymaptestapp.R
import com.example.mymaptestapp.api.utils.DeliveryPointUtils
import com.example.mymaptestapp.api.utils.UserRouteUtils
import com.example.mymaptestapp.data.entities.DeliveryPoint
import com.example.mymaptestapp.databinding.ActivityMapRouteBinding
import com.example.mymaptestapp.utils.JwtTokenUtils
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.modules.TileWriter
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

class MapRouteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapRouteBinding
    private var routeId: Int = 0

    private var points: List<DeliveryPoint> = listOf()
    private var startIndex: Int = 0
    private var endIndex: Int = 0

    private var routeLen: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        routeId = intent.getIntExtra("routeId", 0)

        initStatusBar()
        checkUser()

        binding.apply {
            initAddressText(startPointEt, startPointTitle)
            initAddressText(endPointEt, endPointTitle)
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        initSolution()
        initMap()

        initButtons()
    }

    private fun initStatusBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
    }

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
            val result = JwtTokenUtils.checkTokens(this@MapRouteActivity)

            withContext(Dispatchers.Main) {
                if (!result) {
                    startActivity(Intent(this@MapRouteActivity, LoginActivity::class.java))
                }
            }
        }
    }

    private fun initAddressText(editText: TextInputEditText, textView: TextView) {
        editText.showSoftInputOnFocus = false

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                textView.setTextColor(ContextCompat.getColor(this, R.color.black))
            } else {
                textView.setTextColor(ContextCompat.getColor(this, R.color.grey))
            }
        }
    }

    private fun initSolution() {
        val pointsId = UserRouteUtils.getRoutes().find { userRoute -> userRoute.id == routeId }!!.routePoints
        val mapPoints = DeliveryPointUtils.mapPoints()

        points = List(pointsId.size) { i -> mapPoints.getValue(pointsId[i]) }
        startIndex = 0
        endIndex = 1

        val startPoint = GeoPoint(points[startIndex].latitude, points[startIndex].longitude)
        val endPoint = GeoPoint(points[endIndex].latitude, points[endIndex].longitude)

        binding.mapView.controller.setCenter(startPoint)
        buildRoad(startPoint, endPoint)

        binding.apply {
            leftButton.isClickable = false
            leftButton.setImageResource(R.drawable.icon_white_left_arrow)

            startPointEt.setText(points[startIndex].address)
            endPointEt.setText(points[endIndex].address)

            choiceDescription.text = String.format("%d из %d", startIndex + 1, points.size - 1)
        }
    }

    private fun initMap() {
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        Configuration.getInstance().osmdroidBasePath = filesDir

        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)

        Configuration.getInstance().cacheMapTileCount = 9
        Configuration.getInstance().cacheMapTileOvershoot = 9
        Configuration.getInstance().tileFileSystemCacheMaxBytes = 1024L * 1024L * 50L

        binding.mapView.controller.setZoom(12.0)

        binding.mapView.setMultiTouchControls(true)

        binding.mapView.zoomController
            .setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        binding.mapView.overlays
            .add(RotationGestureOverlay(binding.mapView))
    }

    private fun buildRoad(startPoint: GeoPoint, endPoint: GeoPoint) {
        CoroutineScope(Dispatchers.IO).launch {
            val roadManager = OSRMRoadManager(
                this@MapRouteActivity,
                System.getProperty("http.agent")
            )

            val road = roadManager.getRoad(arrayListOf(startPoint, endPoint))

            val roadOverlay = RoadManager.buildRoadOverlay(
                road,
                Color.parseColor("#1967D2"), 7.5F
            )

            withContext(Dispatchers.Main) {
                binding.mapView.overlays.add(0, roadOverlay)
                routeLen = road.mLength

                setMarker(startPoint, startIndex)
                setMarker(endPoint, endIndex)
            }
        }
    }

    private fun setMarker(geoPoint: GeoPoint, index: Int) {
        val marker = Marker(binding.mapView)
        marker.position = geoPoint
        marker.icon = ContextCompat.getDrawable(this, R.drawable.place)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)

        marker.title = points[index].name
        marker.snippet = String.format("Длина дороги: %.2f км.", routeLen)
        marker.subDescription = points[index].address

        binding.mapView.overlays.add(marker)
    }

    private fun initButtons() {
        binding.leftButton.setOnClickListener {
            binding.startPointEt.clearFocus()
            binding.endPointEt.clearFocus()

            --startIndex
            --endIndex

            val startPoint = GeoPoint(points[startIndex].latitude, points[startIndex].longitude)
            val endPoint = GeoPoint(points[endIndex].latitude, points[endIndex].longitude)

            binding.mapView.overlays.clear()

            binding.mapView.controller.setCenter(startPoint)
            buildRoad(startPoint, endPoint)

            binding.mapView.invalidate()

            binding.apply {
                if (startIndex == 0) {
                    leftButton.isClickable = false
                    leftButton.setImageResource(R.drawable.icon_white_left_arrow)
                }

                if (endIndex != points.size - 1) {
                    rightButton.isClickable = true
                    rightButton.setImageResource(R.drawable.icon_black_right_arrow)
                }

                startPointEt.setText(points[startIndex].address)
                endPointEt.setText(points[endIndex].address)

                choiceDescription.text = String.format("%d из %d", startIndex + 1, points.size - 1)
            }
        }

        binding.rightButton.setOnClickListener {
            binding.startPointEt.clearFocus()
            binding.endPointEt.clearFocus()

            ++startIndex
            ++endIndex

            val startPoint = GeoPoint(points[startIndex].latitude, points[startIndex].longitude)
            val endPoint = GeoPoint(points[endIndex].latitude, points[endIndex].longitude)

            binding.mapView.overlays.clear()

            binding.mapView.controller.setCenter(startPoint)
            buildRoad(startPoint, endPoint)

            binding.mapView.invalidate()

            binding.apply {
                if (startIndex != 0) {
                    leftButton.isClickable = true
                    leftButton.setImageResource(R.drawable.icon_black_left_arrow)
                }

                if (endIndex == points.size - 1) {
                    rightButton.isClickable = false
                    rightButton.setImageResource(R.drawable.icon_white_right_arrow)
                }

                startPointEt.setText(points[startIndex].address)
                endPointEt.setText(points[endIndex].address)

                choiceDescription.text = String.format("%d из %d", startIndex + 1, points.size - 1)
            }
        }
    }
}