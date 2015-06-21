package com.jiriprochazka.weather.android.client;

import com.jiriprochazka.weather.android.entity.ForecastEntity;
import com.jiriprochazka.weather.android.entity.WeatherEntity;

import retrofit.http.GET;
import retrofit.http.Query;


public interface OpenWeatherMapAPI {

    @GET("/data/2.5/weather")
    WeatherEntity getTodaysWeatherCity(
            @Query("q") String city,
            @Query("units") String units
    );


    @GET("/data/2.5/weather")
    WeatherEntity getTodaysWeather(
            @Query("lon") String longitude,
            @Query("lat") String latitude,
            @Query("units") String units
    );


    @GET("/data/2.5/forecast/daily")
    ForecastEntity getForecastCity(
            @Query("q") String city,
            @Query("units") String units
    );


    @GET("/data/2.5/forecast/daily")
    ForecastEntity getForecast(
            @Query("lon") String longitude,
            @Query("lat") String latitude,
            @Query("units") String units
    );
}
