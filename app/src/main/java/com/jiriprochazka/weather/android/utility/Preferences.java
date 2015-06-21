package com.jiriprochazka.weather.android.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jiriprochazka.weather.android.R;

/**
 * Created by jirka on 14.06.15.
 */
public class Preferences {
    public static final int NULL_INT = -1;
    public static final long NULL_LONG = -1l;
    public static final double NULL_DOUBLE = -1.0;
    public static final String NULL_STRING = null;

    private SharedPreferences mSharedPreferences;
    private Context mContext;


    public Preferences(Context context) {
        //if(context==null) context = ExampleApplication.getContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }


    public void clearPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }


    // getters


    public String getUserUnits() {
        String key = mContext.getString(R.string.prefs_key_units);
        String value = mSharedPreferences.getString(key, "metric");
        return value;
    }

    public String getCustomLocation() {
        String key = mContext.getString(R.string.prefs_key_location);
        String value = mSharedPreferences.getString(key, "");
        return value;
    }


    // setters


    public void setUserUnits(String userUnits) {
        String key = mContext.getString(R.string.prefs_key_units);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, userUnits);
        editor.commit();
    }

    public void setCustomLocation(String location) {
        String key = mContext.getString(R.string.prefs_key_location);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, location);
        editor.commit();
    }

}
