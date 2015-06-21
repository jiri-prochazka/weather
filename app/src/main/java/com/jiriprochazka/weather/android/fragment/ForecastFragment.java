package com.jiriprochazka.weather.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.jiriprochazka.weather.android.R;
import com.jiriprochazka.weather.android.adapter.ForecastAdapter;
import com.jiriprochazka.weather.android.entity.ForecastEntity;
import com.jiriprochazka.weather.android.entity.WeatherEntity;
import com.jiriprochazka.weather.android.geolocation.Geolocation;
import com.jiriprochazka.weather.android.listener.AnimateImageLoadingListener;
import com.jiriprochazka.weather.android.listener.GeolocationListener;
import com.jiriprochazka.weather.android.listener.OnLoadDataListener;
import com.jiriprochazka.weather.android.task.LoadForecastTask;
import com.jiriprochazka.weather.android.utility.NetworkManager;
import com.jiriprochazka.weather.android.view.ViewState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


public class ForecastFragment extends TaskFragment implements OnLoadDataListener, GeolocationListener {

    private ViewState mViewState = null;
    private View mRootView;
    private ForecastAdapter mAdapter;
    private LoadForecastTask mLoadForecastTask;

    private List<ForecastEntity.ForecastItem> mForecastList = new ArrayList<>();

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
        mRootView = inflater.inflate(R.layout.fragment_forecast, container, false);
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
            //loadData(); waiting for geolocation
        }
        else if(mViewState==ViewState.CONTENT)
        {
            if(mForecastList !=null) renderView();
            showContent();
        }
        else if(mViewState==ViewState.PROGRESS)
        {
            showProgress();
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

        // stop adapter
        if(mAdapter!=null) mAdapter.stop();
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
        if(mLoadForecastTask !=null) mLoadForecastTask.cancel(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onWeatherLoadData(WeatherEntity result) {

    }

    @Override
    public void onForecastLoadData(final ForecastEntity result)
    {
        runTaskCallback(new Runnable()
        {
            public void run()
            {
                if(mRootView==null) return; // view was destroyed

                // get data
                if (result != null && result.getList() != null) {
                    for (ForecastEntity.ForecastItem e : result.getList()) {
                        mForecastList.add(e);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.toast_null_forecast_text, Toast.LENGTH_LONG).show();
                }

                // render view
                if(mForecastList !=null) renderView();
                showContent();
            }
        });
    }

    @Override
    public void onGeolocationRespond(Geolocation geolocation, final Location location) {
        runTaskCallback(new Runnable() {
            public void run() {
                if (mRootView == null) return; // view was destroyed

                mLocation = new Location(location);
                loadData();
            }
        });
    }

    @Override
    public void onGeolocationFail(Geolocation geolocation) {

    }


    private void loadData()
    {
        if(NetworkManager.isOnline(getActivity()))
        {
            // show progress
            showProgress();
            // run async task
            mLoadForecastTask = new LoadForecastTask(this, mLocation, getActivity());
            executeTask(mLoadForecastTask);
        }
        else
        {
            showOffline();
        }
    }


    private void showContent()
    {
        // show list container
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        containerContent.setVisibility(View.VISIBLE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.GONE);
        mViewState = ViewState.CONTENT;
    }


    private void showProgress()
    {
        // show progress container
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        containerContent.setVisibility(View.GONE);
        containerProgress.setVisibility(View.VISIBLE);
        containerOffline.setVisibility(View.GONE);
        mViewState = ViewState.PROGRESS;
    }


    private void showOffline()
    {
        // show offline container
        ViewGroup containerContent = (ViewGroup) mRootView.findViewById(R.id.container_content);
        ViewGroup containerProgress = (ViewGroup) mRootView.findViewById(R.id.container_progress);
        ViewGroup containerOffline = (ViewGroup) mRootView.findViewById(R.id.container_offline);
        containerContent.setVisibility(View.GONE);
        containerProgress.setVisibility(View.GONE);
        containerOffline.setVisibility(View.VISIBLE);
        mViewState = ViewState.OFFLINE;
    }


    private void renderView()
    {
        // reference
        ListView listView = getListView();
        ViewGroup emptyView = (ViewGroup) mRootView.findViewById(android.R.id.empty);

        // listview content
        if(listView.getAdapter()==null)
        {
            // create adapter
            mAdapter = new ForecastAdapter(getActivity(), mForecastList, mImageLoader, mDisplayImageOptions, mImageLoadingListener);

        }
        else
        {
            // refill adapter
            mAdapter.refill(getActivity(), mForecastList);
        }

        // set adapter
        listView.setAdapter(mAdapter);

        // listview empty view
        listView.setEmptyView(emptyView);

    }


    private ListView getListView()
    {
        return mRootView!=null ? (ListView) mRootView.findViewById(android.R.id.list) : null;
    }

}

