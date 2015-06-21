package com.jiriprochazka.weather.android.listener;


import com.jiriprochazka.weather.android.entity.ForecastEntity;
import com.jiriprochazka.weather.android.entity.WeatherEntity;

import java.util.List;

public interface OnLoadDataListener
{
	public void onWeatherLoadData(WeatherEntity result);
	public void onForecastLoadData(ForecastEntity result);
}
