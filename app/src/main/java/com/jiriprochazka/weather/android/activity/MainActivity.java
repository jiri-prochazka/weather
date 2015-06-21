package com.jiriprochazka.weather.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jiriprochazka.weather.android.R;
import com.jiriprochazka.weather.android.adapter.DrawerAdapter;
import com.jiriprochazka.weather.android.dialog.AboutDialogFragment;
import com.jiriprochazka.weather.android.fragment.ForecastFragment;
import com.jiriprochazka.weather.android.fragment.SettingsFragment;
import com.jiriprochazka.weather.android.fragment.TodayFragment;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerListView;

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String[] mTitles;

    public static Intent newIntent(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActionBar();
        setUpImageLoader();
        setupDrawer(savedInstanceState);
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
    }


    @Override
    public void onStop()
    {
        super.onStop();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // action bar menu visibility
        if(menu!=null)
        {
            boolean drawerOpened = mDrawerLayout.isDrawerOpen(mDrawerListView);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // action bar menu
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // open or close the drawer if home button is pressed
        if(mDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        // action bar menu behaviour
        switch(item.getItemId())
        {
            case android.R.id.home:
                return true;

            case R.id.action_about:
                showAboutDialog();
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfiguration)
    {
        super.onConfigurationChanged(newConfiguration);
        mDrawerToggle.onConfigurationChanged(newConfiguration);
    }


    @Override
    public void onBackPressed()
    {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
        {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        else
        {
            super.onBackPressed();
        }
    }


    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }


    private void setupActionBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
    }


    private void setupDrawer(Bundle savedInstanceState)
    {
        mTitle = getTitle();
        mDrawerTitle = getTitle();

        mTitles = new String[2];
        mTitles[0] = getString(R.string.drawer_item_today);
        mTitles[1] = getString(R.string.drawer_item_forecast);


        Integer[] icons = new Integer[2];
        icons[0] = R.mipmap.ic_action_today;
        icons[1] = R.mipmap.ic_action_forecast;


        // reference
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_layout);
        mDrawerListView = (ListView) findViewById(R.id.activity_main_drawer);

        // set drawer
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerListView.setAdapter(new DrawerAdapter(this, mTitles, icons));
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View clickedView, int position, long id)
            {
                selectDrawerItem(position);
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close)
        {
            @Override
            public void onDrawerClosed(View view)
            {
                getSupportActionBar().setTitle(mTitle);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                getSupportActionBar().setTitle(mDrawerTitle);
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // show initial fragment
        if(savedInstanceState == null)
        {
            selectDrawerItem(0);
        }
    }


    private void selectDrawerItem(int position)
    {
        Fragment fragment;
        if(position==0) fragment = new TodayFragment();
        else if(position==1) fragment = new ForecastFragment();
        else fragment = new TodayFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.activity_main_container, fragment).commitAllowingStateLoss();

        mDrawerListView.setItemChecked(position, true);
        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerListView);
    }

    private void showAboutDialog()
    {
        // create and show the dialog
        new AboutDialogFragment().show(getFragmentManager(), "About Dialog");

    }

    private void setUpImageLoader(){
        // init image caching
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        cacheDir.mkdirs(); // requires android.permission.WRITE_EXTERNAL_STORAGE

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        ImageLoader.getInstance().init(config);
    }

}
