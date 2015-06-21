package com.jiriprochazka.weather.android.task;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.jiriprochazka.weather.android.client.OpenWeatherMapAPI;
import com.jiriprochazka.weather.android.entity.WeatherEntity;
import com.jiriprochazka.weather.android.listener.OnLoadDataListener;
import com.jiriprochazka.weather.android.utility.Preferences;

import java.lang.ref.WeakReference;
import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


public class LoadTodayTask extends AsyncTask<Void, Void, String>
{
	private WeakReference<OnLoadDataListener> mOnLoadDataListener;
	private WeatherEntity mWeather;
	private Location mLocation;
	private Context mContext;
	
	
	public LoadTodayTask(OnLoadDataListener onLoadDataListener, Location location, Context context) {
		setListener(onLoadDataListener);
		this.mLocation = location;
		this.mContext = context;
	}
	
	
	@Override
	protected String doInBackground(Void... params) {
		try {
			Preferences prefs = new Preferences(mContext);
			String units = prefs.getUserUnits();
			String customLocation = prefs.getCustomLocation();
			String longitude = String.valueOf(mLocation.getLongitude());
			String latitude = String.valueOf(mLocation.getLatitude());

			Gson gson = new GsonBuilder()
					.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
					.registerTypeAdapter(Date.class, new DateTypeAdapter())
					.create();

			RestAdapter restAdapter = new RestAdapter.Builder()
					.setEndpoint("http://api.openweathermap.org")
					.setConverter(new GsonConverter(gson))
					.build();

			OpenWeatherMapAPI api = restAdapter.create(OpenWeatherMapAPI.class);
			if(customLocation == null || customLocation.isEmpty()) {
				mWeather = api.getTodaysWeather(longitude, latitude, units);
			} else {
				mWeather = api.getTodaysWeatherCity(customLocation, units);
			}
			Log.d("DEBUG", prefs.getCustomLocation());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	protected void onPostExecute(String result) {
		if(isCancelled()) return;
		
		OnLoadDataListener listener = mOnLoadDataListener.get();
		if(listener != null) {
			listener.onWeatherLoadData(mWeather);
		}
	}
	
	
	public void setListener(OnLoadDataListener onLoadDataListener) {
		mOnLoadDataListener = new WeakReference<>(onLoadDataListener);
	}
}
