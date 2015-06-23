package com.jiriprochazka.weather.android.geolocation;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jiriprochazka.weather.android.listener.GeolocationListener;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class Geolocation implements LocationListener
{
    private static final int LOCATION_AGE = 60000 * 30; // milliseconds
    private static final int LOCATION_TIMEOUT = 30000; // milliseconds

    private WeakReference<GeolocationListener> mListener;
    private LocationManager mLocationManager;
    private Location mCurrentLocation;
    private Timer mTimer;


    public Geolocation(LocationManager locationManager, GeolocationListener listener)
    {
        mLocationManager = locationManager; // (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mListener = new WeakReference<>(listener);
        mTimer = new Timer();
        init();
    }


    @Override
    public void onLocationChanged(Location location)
    {
        // check location age
        long timeDelta = System.currentTimeMillis() - location.getTime();
        if(timeDelta > LOCATION_AGE)
        {
            //Logcat.d("Geolocation.onLocationChanged(): gotten location is too old");
            // gotten location is too old
            return;
        }

        // return location
        mCurrentLocation = new Location(location);
        stop();
        GeolocationListener listener = mListener.get();
        if(listener!=null && location!=null) listener.onGeolocationRespond(Geolocation.this, mCurrentLocation);
    }


    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d("GPS", "DISABLED");
        GeolocationListener listener = mListener.get();
        if(listener!=null) listener.onGeolocationDisabled(Geolocation.this);
    }


    @Override
    public void onProviderEnabled(String provider)
    {
       // Logcat.d("Geolocation.onProviderEnabled(): " + provider);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        switch(status)
        {
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("GPS", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("GPS", "TEMP_N/A");
                break;
            case LocationProvider.AVAILABLE:
                break;
        }
    }


    public void stop()
    {
        if(mTimer!=null) mTimer.cancel();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(this);
            mLocationManager = null;
        }
    }


    private void init()
    {
        // get last known location
        Location lastKnownLocation = getLastKnownLocation(mLocationManager);

        // try to listen last known location
        if(lastKnownLocation != null)
        {
            onLocationChanged(lastKnownLocation);
        }

        if(mCurrentLocation == null)
        {
            // start timer to check timeout
            TimerTask task = new TimerTask()
            {
                public void run()
                {
                    if(mCurrentLocation == null)
                    {
                        //Logcat.d("Geolocation.timer: timeout");
                        stop();
                        GeolocationListener listener = mListener.get();
                        if(listener != null) listener.onGeolocationFail(Geolocation.this);
                    }
                }
            };
            mTimer.schedule(task, LOCATION_TIMEOUT);

            // register location updates
            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0.0f, this);
            }
            catch(IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            try
            {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0.0f, this);
            }
            catch(IllegalArgumentException e)
            {
                e.printStackTrace();
                stop();
                GeolocationListener listener = mListener.get();
                if(listener != null) listener.onGeolocationFail(Geolocation.this);
            }
        }
    }


    // returns last known freshest location from network or GPS
    private Location getLastKnownLocation(LocationManager locationManager)
    {

        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        long timeNet = 0l;
        long timeGps = 0l;

        if(locationNet!=null)
        {
            timeNet = locationNet.getTime();
        }

        if(locationGps!=null)
        {
            timeGps = locationGps.getTime();
        }

        if(timeNet>timeGps) return locationNet;
        else return locationGps;
    }
}
