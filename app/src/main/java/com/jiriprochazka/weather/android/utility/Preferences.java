package com.jiriprochazka.weather.android.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.jiriprochazka.weather.android.R;

public class Preferences {

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
