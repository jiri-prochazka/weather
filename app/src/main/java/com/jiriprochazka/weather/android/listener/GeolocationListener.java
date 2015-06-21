package com.jiriprochazka.weather.android.listener;

import android.location.Location;

import com.jiriprochazka.weather.android.geolocation.Geolocation;

/**
 * Created by jirka on 18.06.15.
 */
public interface GeolocationListener
{
    public void onGeolocationRespond(Geolocation geolocation, Location location);
    public void onGeolocationFail(Geolocation geolocation);
}
