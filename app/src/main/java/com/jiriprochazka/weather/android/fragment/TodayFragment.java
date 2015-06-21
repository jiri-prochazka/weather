package com.jiriprochazka.weather.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiriprochazka.weather.android.R;
import com.jiriprochazka.weather.android.entity.ForecastEntity;
import com.jiriprochazka.weather.android.entity.Weather;
import com.jiriprochazka.weather.android.entity.WeatherEntity;
import com.jiriprochazka.weather.android.geolocation.Geolocation;
import com.jiriprochazka.weather.android.listener.AnimateImageLoadingListener;
import com.jiriprochazka.weather.android.listener.GeolocationListener;
import com.jiriprochazka.weather.android.listener.OnLoadDataListener;
import com.jiriprochazka.weather.android.task.LoadTodayTask;
import com.jiriprochazka.weather.android.utility.NetworkManager;
import com.jiriprochazka.weather.android.utility.Preferences;
import com.jiriprochazka.weather.android.view.ViewState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Date;
import java.util.List;


public class TodayFragment extends TaskFragment implements OnLoadDataListener, GeolocationListener
{
	private ViewState mViewState = null;
	private View mRootView;
	private LoadTodayTask mLoadTodayTask;
	private WeatherEntity mWeather;

	private ImageLoader mImageLoader = ImageLoader.getInstance();
	private DisplayImageOptions mDisplayImageOptions;
	private ImageLoadingListener mImageLoadingListener;

	private Geolocation mGeolocation = null;
	private Location mLocation = null;
	
	
	@Override
	public void onAttach(Activity activity) 
	{
		super.onAttach(activity);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		// image caching options
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(android.R.color.transparent)
				.showImageForEmptyUri(android.R.drawable.ic_menu_report_image)
				.showImageOnFail(android.R.drawable.ic_delete)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new SimpleBitmapDisplayer())
				.build();
		mImageLoadingListener = new AnimateImageLoadingListener();
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.fragment_today, container, false);
		return mRootView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		// start geolocation
		if(mLocation==null)
		{
			mGeolocation = null;
			mGeolocation = new Geolocation((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE), this);
		}
		
		// load and show data
		if(mViewState==null || mViewState==ViewState.OFFLINE)
		{
			//loadData();
		}
		else if(mViewState==ViewState.CONTENT)
		{
			if(mWeather!=null) renderView();
			showContent();
		}
		else if(mViewState==ViewState.PROGRESS)
		{
			showProgress();
		}
		else if(mViewState==ViewState.EMPTY)
		{
			showEmpty();
		}
	}
	
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
	}
	
	
	@Override
	public void onPause()
	{
		super.onPause();
		// stop geolocation
		if(mGeolocation!=null) mGeolocation.stop();
	}
	
	
	@Override
	public void onStop()
	{
		super.onStop();
	}
	
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		mRootView = null;
	}
	
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		// cancel async tasks
		if(mLoadTodayTask !=null) mLoadTodayTask.cancel(true);
	}
	
	
	@Override
	public void onDetach()
	{
		super.onDetach();
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// save current instance state
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// action bar menu
		super.onCreateOptionsMenu(menu, inflater);
		
		// TODO
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// action bar menu behaviour
		return super.onOptionsItemSelected(item);
		
		// TODO
	}
	
	
	@Override
	public void onWeatherLoadData(WeatherEntity result) {
		mWeather = result;
		runTaskCallback(new Runnable() {
			public void run() {
				if (mRootView == null) return; // view was destroyed

				// hide progress and render view
				if (mWeather != null && mWeather.getMain() != null) {
					renderView();
					showContent();
				} else {
					showEmpty();
					Toast.makeText(getActivity(), R.string.toast_null_forecast_text, Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public void onForecastLoadData(ForecastEntity result) {

	}

	@Override
	public void onGeolocationRespond(Geolocation geolocation, final Location location)
	{
		runTaskCallback(new Runnable()
		{
			public void run()
			{
				if(mRootView==null) return; // view was destroyed

				mLocation = new Location(location);
				loadData();
				// TODO
			}
		});
	}


	@Override
	public void onGeolocationFail(Geolocation geolocation)
	{
		runTaskCallback(new Runnable()
		{
			public void run()
			{
				if(mRootView==null) return; // view was destroyed

				//Logcat.d("Fragment.onGeolocationFail()");

				// TODO
			}
		});
	}


	private void loadData()
	{
		if(NetworkManager.isOnline(getActivity()))
		{
			// show progress
			showProgress();
			// run async task
			mLoadTodayTask = new LoadTodayTask(this, mLocation, getActivity());
			executeTask(mLoadTodayTask);
		}
		else
		{
			showOffline();
		}
	}
	
	
	private void showContent()
	{
		// show content container
		ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
		ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
		ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
		ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
		containerContent.setVisibility(View.VISIBLE);
		containerProgress.setVisibility(View.GONE);
		containerOffline.setVisibility(View.GONE);
		containerEmpty.setVisibility(View.GONE);
		mViewState = ViewState.CONTENT;
	}
	
	
	private void showProgress()
	{
		// show progress container
		ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
		ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
		ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
		ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
		containerContent.setVisibility(View.GONE);
		containerProgress.setVisibility(View.VISIBLE);
		containerOffline.setVisibility(View.GONE);
		containerEmpty.setVisibility(View.GONE);
		mViewState = ViewState.PROGRESS;
	}
	
	
	private void showOffline()
	{
		// show offline container
		ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
		ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
		ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
		ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
		containerContent.setVisibility(View.GONE);
		containerProgress.setVisibility(View.GONE);
		containerOffline.setVisibility(View.VISIBLE);
		containerEmpty.setVisibility(View.GONE);
		mViewState = ViewState.OFFLINE;
	}
	
	
	private void showEmpty()
	{
		// show empty container
		ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
		ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
		ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
		ViewGroup containerEmpty = (ViewGroup) mRootView.findViewById(R.id.container_empty);
		containerContent.setVisibility(View.GONE);
		containerProgress.setVisibility(View.GONE);
		containerOffline.setVisibility(View.GONE);
		containerEmpty.setVisibility(View.VISIBLE);
		mViewState = ViewState.EMPTY;
	}
	
	
	private void renderView()
	{
		// reference
		TextView humidityTextView = (TextView) mRootView.findViewById(R.id.humidity_icon_text);
		TextView precipitationTextView = (TextView) mRootView.findViewById(R.id.precipitation_icon_text);
		TextView pressureTextView = (TextView) mRootView.findViewById(R.id.pressure_icon_text);
		TextView windTextView = (TextView) mRootView.findViewById(R.id.wind_icon_text);
		TextView directionTextView = (TextView) mRootView.findViewById(R.id.direction_icon_text);
		TextView tempTextView = (TextView) mRootView.findViewById(R.id.temp_text);
		TextView locationTextView = (TextView) mRootView.findViewById(R.id.location_text);
		TextView forecastTextView = (TextView) mRootView.findViewById(R.id.forecast_text);

		ImageView bgImageView = (ImageView) mRootView.findViewById(R.id.today_bg);

		String units = new Preferences(getActivity()).getUserUnits();

		// content
		humidityTextView.setText(mWeather.getMain().getHumidity() + "%");
		precipitationTextView.setText(mWeather.getPrecipitationString() + " mm");
		pressureTextView.setText(mWeather.getMain().getPressure() + " hPa");
		windTextView.setText(mWeather.getWind().getSpeed() + (units.equals("imperial") ? " mph" : " m/s"));
		directionTextView.setText(mWeather.getWind().compassDegrees());

		Weather weather1 = mWeather.getWeatherItems().get(0);

		tempTextView.setText(mWeather.getMain().getRoundedTempString()+"°");
		locationTextView.setText(mWeather.getName());
		forecastTextView.setText(weather1.getDescription());

		// set background according to weather conditions
		if (weather1.getIcon().startsWith("01") || weather1.getIcon().startsWith("02"))
			bgImageView.setImageResource(R.mipmap.bg_sunny);
		if (weather1.getIcon().startsWith("09") || weather1.getIcon().startsWith("10") || weather1.getIcon().startsWith("11"))
			bgImageView.setImageResource(R.mipmap.bg_rain);

		// reference
		ImageView photoImageView = (ImageView) mRootView.findViewById(R.id.imageView);

		// image caching
		mImageLoader.displayImage("http://openweathermap.org/img/w/"+weather1.getIcon()+".png", photoImageView, mDisplayImageOptions, mImageLoadingListener);

	}
}
