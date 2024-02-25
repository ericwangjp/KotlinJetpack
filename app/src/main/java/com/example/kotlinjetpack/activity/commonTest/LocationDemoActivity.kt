package com.example.kotlinjetpack.activity.commonTest

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kotlinjetpack.databinding.ActivityLocationDemoBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class LocationDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationDemoBinding
    private val locationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private lateinit var locationListener: LocationListener
    private val resultCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                checkPermission()
            }
        }

    private val permissionCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            for ((k, v) in it) {
                if (!v) {
                    Toast.makeText(this, "$k 权限被拒绝", Toast.LENGTH_SHORT).show()
                }
            }
            if (it.values.all { true }) {
//                所有权限授权通过
                initLocation()
            }
        }
    private val mainScope by lazy {
        MainScope()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnStartLocation.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            ))
        ) {
            Toast.makeText(this, "请打开网络或者GPS定位功能！", Toast.LENGTH_SHORT).show()
            resultCallback.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionCallback.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else {
            initLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
// 优先获取gps定位结果，没有的话再获取网络定位结果
        val minTime = 2 * 1000L
        val minDistance = 2F
        var lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (lastKnownLocation == null) {
            lastKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        lastKnownLocation?.let {
            updateLocation(it)
        }

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                Log.e("onLocationChanged: ", "位置改变")
                updateLocation(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
                when (status) {
                    LocationProvider.AVAILABLE -> {
                        Log.e("onStatusChanged: ", "当前GPS为可见状态")
                    }
                    LocationProvider.OUT_OF_SERVICE -> {
                        Log.e("onStatusChanged: ", "当前GPS为服务区外状态")
                    }
                    LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                        Log.e("onStatusChanged: ", "当前GPS为暂停服务状态")
                    }
                }
            }

            override fun onProviderEnabled(provider: String) {
                super.onProviderEnabled(provider)
                Log.e("onProviderEnabled: ", "服务可用了")
                locationManager.getLastKnownLocation(provider)?.apply {
                    updateLocation(this)
                }
            }

            override fun onProviderDisabled(provider: String) {
                super.onProviderDisabled(provider)
                Log.e("onProviderEnabled: ", "服务不可用了")
            }
        }

        // 开始定位
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener
        )

        // 卫星监听
        locationManager.addGpsStatusListener { event ->
            when (event) {
                GpsStatus.GPS_EVENT_FIRST_FIX -> Log.e("initLocation: ", "第一次卫星定位")
                GpsStatus.GPS_EVENT_SATELLITE_STATUS -> {
                    Log.e("initLocation: ", "卫星状态改变")
                    val gpsStatus = locationManager.getGpsStatus(null)
                    val maxSatellites = gpsStatus?.maxSatellites ?: 0
                    val iterator = gpsStatus?.satellites?.iterator()
                    var count = 0
                    while (iterator?.hasNext() == true && count <= maxSatellites) {
                        val gpsSatellite = iterator.next()
                        // 只有信噪比不为0时才算合格的卫星
                        if (gpsSatellite.snr != 0F) {
                            count++
                        }
                    }

                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    var curTime = simpleDateFormat.format(Date())
                    binding.tvResult1.text = "当前时间：$curTime, \n卫星数量：$count"

                }
                GpsStatus.GPS_EVENT_STARTED -> {
                    Log.e("initLocation: ", "卫星定位启动")

                }
                GpsStatus.GPS_EVENT_STOPPED -> {
                    Log.e("initLocation: ", "卫星定位停止")

                }
            }
        }
    }

    private fun updateLocation(location: Location) {
        mainScope.launch(Dispatchers.IO) {
            val latitude = location.latitude
            val longitude = location.longitude
            val altitude = location.altitude
            val accuracy = location.accuracy
            val time = location.time
            val mock = false
//        location.isMock
            val provider = location.provider
            val bearing = location.bearing
            val extras = location.extras
            val geocoder = Geocoder(this@LocationDemoActivity)
            val present = Geocoder.isPresent()
            Log.e("地理位置解码是否可用：", "->${present} ")
            // 这里必须子线程处理,否则获取失败
            val address = if (present) {
                val addressResult = async {
                    Log.e("线程名称：", "->${Thread.currentThread().name}")
                    try {
                        geocoder.getFromLocation(latitude, longitude, 10)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                addressResult.await()

            } else {
                null
            }

            for (k in extras!!.keySet()) {
                Log.e("定位信息:", "key:$k-->value:${extras[k]}: ")
            }
//        val speedAccuracyMetersPerSecond = if (location.hasSpeedAccuracy()){
//            location.speedAccuracyMetersPerSecond
//        } else {
//            0
//        }
            val speed = if (location.hasSpeed()) {
                // 换算为 千米/时
                location.speed * 3.6
            } else {
                0F
            }
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var result =
                "实时时间：${simpleDateFormat.format(Date(time))} \n纬度： $latitude \n经度： $longitude \n速度： ${
                    String.format(
                        "%.2f", speed
                    )
                } km/h\n海拔高度：$altitude \n精度：$accuracy \n是否模拟位置：$mock \n定位方式：$provider \n 方位：$bearing \n"

            if (!address.isNullOrEmpty()) {
                address.forEach {
                    result += "国家编码：${it.countryCode}-countryName:${it.countryName}  " + "localCountry:${it.locale.displayCountry} -语言displayName:${it.locale.displayName}  " + "省:${it.adminArea}-城市:${it.subAdminArea}  " + "区/县：${it.locality}  " + "premises:${it.premises} -详细地址(区往下):${it.featureName}  " + "语言displayName:${it.locale.displayLanguage} ${it.locale.language} ${it.locale.displayScript} ${it.locale.displayVariant}-subLocality:${it.subLocality}  " + "大道/大街名thoroughfare:${it.thoroughfare}-子subThoroughfare:${it.subThoroughfare}  " + "(省市区详细地址)addressLine:${
                        it.getAddressLine(
                            0
                        )
                    }\n\n"
                }
            }
            withContext(Dispatchers.Main) {
                binding.tvResult2.text = result
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //关闭时程序时，移除监听器
        if (::locationListener.isInitialized) {
            locationManager.removeUpdates(locationListener)
        }
        mainScope.cancel()
    }
}