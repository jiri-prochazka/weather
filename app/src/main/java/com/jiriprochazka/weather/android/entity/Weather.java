package com.jiriprochazka.weather.android.entity;

/**
 * Created by jirka on 16.06.15.
 */
public class Weather {
    private int id;
    private String main;
    private String description;
    private String icon;

    public int getId() {
        return id;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}