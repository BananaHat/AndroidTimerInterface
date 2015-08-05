package com.httpeffectivemobilesolutions.timerinterface.controller;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.httpeffectivemobilesolutions.timerinterface.R;
import com.httpeffectivemobilesolutions.timerinterface.model.TimerModel;
import com.httpeffectivemobilesolutions.timerinterface.view.NavigationDrawerFragment;
import com.httpeffectivemobilesolutions.timerinterface.view.TimerContentFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG = MainActivity.class.getSimpleName();
    private static String CONTENT_KEY = "CONTENT_KEY";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Fragment that shows the necessary data bout the selected timer
     */
    private TimerContentFragment mTimerContentFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     *
     */
    private NetworkController mNetworkController;
    /**
     *
     */
    private int mActiveTimer;

    //**********************************************************************************************
    // OVERRIDES
    //*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        WifiManager wifi;
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock mLock = wifi.createMulticastLock("lock");
        mLock.acquire();
        mNetworkController = new NetworkController(this, new Handler());
        mNetworkController.broadcastListen(10000);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        mActiveTimer = position;
        if(null == mTimerContentFragment) {
            mTimerContentFragment = new TimerContentFragment();
            //mTimerContentFragment.setTimer(TimerModel.getTimers().get(position));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, mTimerContentFragment)
                    .commit();
        }
        mTimerContentFragment.setTimer(TimerModel.getTimers().get(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //**********************************************************************************************
    // PUBLIC METHODS
    //*********************************************************************************************/

    public void update(){
        mNavigationDrawerFragment.UpdateList();
        if(TimerModel.getTimers().size() > mActiveTimer && null != mTimerContentFragment) {
            mTimerContentFragment.setTimer(TimerModel.getTimers().get(mActiveTimer));
        }
    }

    public NetworkController getNetworkController(){
        return mNetworkController;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void setTimerContentFragment(TimerContentFragment fragment){
        mTimerContentFragment = fragment;
    }

}
