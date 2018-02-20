package com.sample.mybar.ui;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sample.mybar.BarApplication;
import com.sample.mybar.R;
import com.sample.mybar.events.map.ShowMarkerEvent;
import com.sample.mybar.events.map.UpdateCameraEvent;
import com.sample.mybar.events.map.UpdateUiEvent;
import com.sample.mybar.ui.adapters.SectionsPagerAdapter;
import com.sample.mybar.ui.fragments.MapFragment;
import com.sample.mybar.utils.ApiManager;
import com.sample.mybar.utils.common.BarPresentData;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements PermissionCheck, OnListBarClickedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mIsLocationPermissionGranted;
    @Inject
    FusedLocationProviderClient mFusedLocationProviderClient;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Inject dependencies
        BarApplication.get(this).getLocationComponent().inject(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(), this));

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getLocationPermission();

        EventBus.getDefault().post(new UpdateUiEvent());

        getDeviceLocation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            getDeviceLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean locationPermissionGranted() {
        return mIsLocationPermissionGranted;
    }

    @Override
    public void onListBarClick(BarPresentData bar) {
        mViewPager.setCurrentItem(SectionsPagerAdapter.SECOND_PAGE);
        EventBus.getDefault().post(new ShowMarkerEvent(bar));
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mIsLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mIsLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mIsLocationPermissionGranted = true;
                }
            }
        }
        EventBus.getDefault().post(new UpdateUiEvent());
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mIsLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener( this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location lastKnownLocation = task.getResult();
                            EventBus.getDefault().post(
                                    new UpdateCameraEvent(
                                            new LatLng(lastKnownLocation.getLatitude(),
                                                    lastKnownLocation.getLongitude())));

                            locationObtained(new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude()));

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            EventBus.getDefault().post(
                                    new UpdateCameraEvent(MapFragment.MAP_DEFAULT_LAT_LNG));

                            showSnackBar();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void locationObtained(LatLng latLng) {
        new ApiManager(this).getNearbyBars(latLng);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content),
                R.string.gps_data_not_found, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_refresh, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        snackbar.show();
    }


}
