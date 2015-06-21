package com.jiriprochazka.weather.android.entity;

import java.util.List;

public class ForecastEntity {

    private List<ForecastItem> list;

    public List<ForecastItem> getList() {
        return list;
    }

    public class Temp {
        private float day;
        private float min;
        private float max;
        private float night;
        private float eve;
        private float morn;

        public float getDay() {
            return day;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        public float getNight() {
            return night;
        }

        public float getEve() {
            return eve;
        }

        public float getMorn() {
            return morn;
        }

        public String getRoundedTempString() {
            return String.valueOf(Math.round(day));
        }
    }

    public class ForecastItem {
        private long dt;
        private Temp temp;
        private float pressure;
        private int humidity;
        private List<Weather> weather;
        private float speed;
        private int deg;
        private int clouds;

        public long getDt() {
            return dt;
        }

        public Temp getTemp() {
            return temp;
        }

        public float getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public float getSpeed() {
            return speed;
        }

        public int getDeg() {
            return deg;
        }

        public int getClouds() {
            return clouds;
        }
    }
}
