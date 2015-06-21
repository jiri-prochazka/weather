package com.jiriprochazka.weather.android.entity;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherEntity {


    private String name;
    private Wind wind;
    private Main main;
    private Rain rain;
    private ArrayList<Weather> weather;

    public Wind getWind() {
        return wind;
    }

    public Rain getRain() {
        return rain;
    }

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Weather> getWeatherItems() {
        return weather;
    }


    public String getPrecipitationString() {
        if(rain == null) {
            return "0";
        } else {
            return rain.getPrecipitation();
        }
    }

    // Classes of sub-responses

    public class Main {
        private float temp;
        private float tempMin;
        private float tempMax;
        private float pressure;
        private int humidity;

        public float getTemp() {
            return temp;
        }

        public String getRoundedTempString() {
            return String.valueOf(Math.round(temp));
        }

        public float getTempMin() {
            return tempMin;
        }

        public float getTempMax() {
            return tempMax;
        }

        public float getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }
    }

    public class Wind {
        private String speed;
        private float deg;

        public String getSpeed() {
            return speed;
        }

        public float getDeg() {
            return deg;
        }

        public String compassDegrees() {
            String[] directions = {"N","NNE","NE","ENE","E","ESE", "SE", "SSE","S","SSW","SW","WSW","W","WNW","NW","NNW"};
            int val = (int) ((deg/22.5)+0.5);
            return directions[(val % 16)];
        }
    }

    public class Rain {
        @SerializedName("3h")
        public String precipitation;

        public String getPrecipitation() {
            return precipitation;
        }

    }

}


