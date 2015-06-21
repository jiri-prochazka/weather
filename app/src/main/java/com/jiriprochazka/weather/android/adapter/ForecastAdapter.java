package com.jiriprochazka.weather.android.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiriprochazka.weather.android.R;
import com.jiriprochazka.weather.android.entity.ForecastEntity;
import com.jiriprochazka.weather.android.entity.Weather;
import com.jiriprochazka.weather.android.utility.Preferences;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jirka on 15.06.15.
 */
public class ForecastAdapter extends BaseAdapter {
    private Context mContext;
    private List<ForecastEntity.ForecastItem> mForecastList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mDisplayImageOptions;
    private ImageLoadingListener mImageLoadingListener;
    private String units;


    public ForecastAdapter(Context context, List<ForecastEntity.ForecastItem> forecastList, ImageLoader mImageLoader, DisplayImageOptions mDisplayImageOptions, ImageLoadingListener mImageLoadingListener)
    {
        mContext = context;
        mForecastList = forecastList;
        this.mImageLoader = mImageLoader;
        this.mDisplayImageOptions = mDisplayImageOptions;
        this.mImageLoadingListener = mImageLoadingListener;
        this.units = new Preferences(context).getUserUnits();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // inflate view
        View view = convertView;
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_forecast_item, parent, false);

            // view holder
            ViewHolder holder = new ViewHolder();
            holder.iconImageView = (ImageView) view.findViewById(R.id.fragment_forecast_item_icon);
            holder.mainTextView = (TextView) view.findViewById(R.id.fragment_forecast_item_text);
            holder.tempTextView = (TextView) view.findViewById(R.id.fragment_forecast_item_temp);
            view.setTag(holder);
        }

        // entity
        ForecastEntity.ForecastItem forecast = mForecastList.get(position);

        if(forecast != null)
        {
            // view holder
            ViewHolder holder = (ViewHolder) view.getTag();
            Weather weather1 = forecast.getWeather().get(0);

            // create text lines of item
            String mainText = weather1.getMain() + getDayOfForecast(position);
            String tempText = forecast.getTemp().getRoundedTempString()+"°";
            tempText += (units.equals("imperial") ? "F" : "C");
            tempText += ", " + weather1.getDescription();

            holder.mainTextView.setText(mainText);
            holder.tempTextView.setText(tempText);

            // load weather icon
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + weather1.getIcon() + ".png", holder.iconImageView, mDisplayImageOptions, mImageLoadingListener);
        }

        return view;
    }


    @Override
    public int getCount()
    {
        if(mForecastList !=null) return mForecastList.size();
        else return 0;
    }


    @Override
    public Object getItem(int position)
    {
        if(mForecastList !=null) return mForecastList.get(position);
        else return null;
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }


    public void refill(Context context, List<ForecastEntity.ForecastItem> forecastList)
    {
        mContext = context;
        mForecastList = forecastList;
        notifyDataSetChanged();
    }


    public void stop()
    {
        // TODO: stop image loader
    }


    static class ViewHolder
    {
        ImageView iconImageView;
        TextView mainTextView;
        TextView tempTextView;
    }

    private String getDayOfForecast(int position) {
        String text = " on ";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        cal.add(Calendar.DAY_OF_WEEK, position);
        text += sdf.format(cal.getTime());
        return text;
    }
}
