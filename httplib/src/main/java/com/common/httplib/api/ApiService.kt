package com.common.httplib.api

import com.common.httplib.model.WeatherInfo
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * desc: ApiService
 * Author: fengqy
 * Version: V1.0.0
 * Create: 2022/6/11 4:50 下午
 */
class ApiService {

    interface WeatherApiService {
        @GET("weather/now.json")
        suspend fun getWeatherInfoNow(@Query("location") location: String): WeatherInfo
    }

}