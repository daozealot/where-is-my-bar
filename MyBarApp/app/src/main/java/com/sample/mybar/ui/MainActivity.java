package com.sample.mybar.ui;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sample.mybar.BarApplication;
import com.sample.mybar.R;
import com.sample.mybar.api.GoogleDistanceApi;
import com.sample.mybar.api.GooglePlacesApi;
import com.sample.mybar.utils.common.BarPresentData;
import com.sample.mybar.api.model.distance.Row;
import com.sample.mybar.api.model.distance.RowsWrapper;
import com.sample.mybar.api.model.places.Result;
import com.sample.mybar.api.model.places.ResultsWrapper;
import com.sample.mybar.events.BarsReceivedEvent;
import com.sample.mybar.events.DistanceReceivedEvent;
import com.sample.mybar.events.map.UpdateCameraEvent;
import com.sample.mybar.events.map.UpdateUiEvent;
import com.sample.mybar.ui.adapters.SectionsPagerAdapter;
import com.sample.mybar.ui.fragments.MapFragment;
import com.sample.mybar.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements PermissionCheck {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(42.697032, 23.321040);

    @Inject
    Retrofit mRetrofit;

    @Inject
    FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mIsLocationPermissionGranted;
    private Location mLastKnownLocation;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setContentView(R.layout.activity_main);

        // Inject dependencies to Activity
        BarApplication.get(this).getAppComponent().inject(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the two
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getLocationPermission();

        EventBus.getDefault().post(new UpdateUiEvent());

        getDeviceLocation();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mLastKnownLocation != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
        }
        super.onSaveInstanceState(outState);
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
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastKnownLocation = task.getResult();
                            EventBus.getDefault().post(
                                    new UpdateCameraEvent(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude())));

                            getNearbyBars();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            EventBus.getDefault().post(
                                    new UpdateCameraEvent(MapFragment.MAP_DEFAULT_LAT_LNG));

                            getLocationPermission();

                            // FIXME debug only
                            getNearbyBars();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getNearbyBars() {
        final LatLng latLng = mLastKnownLocation != null ?
                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()) :
                mDefaultLocation;

        mRetrofit.create(GooglePlacesApi.class)
                .getNearbyBars(
                        latLng.latitude + "," + latLng.longitude,
                        "bar",
                        "distance",
                        getString(R.string.google_maps_key))
                .enqueue(new Callback<ResultsWrapper<Result>>() {
                    @Override
                    public void onResponse(Call<ResultsWrapper<Result>> call, Response<ResultsWrapper<Result>> response) {
                        if (response.isSuccessful()) {
                            List<BarPresentData> barData = Utils.convertResponseToBarData(response);
                            EventBus.getDefault().post(new BarsReceivedEvent(barData));
                            getBarDistances(latLng, barData);
                        } else {
                            Log.d("PlacesServiceCallback", "Code: " + response.code() + " Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultsWrapper<Result>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void getBarDistances(LatLng lastLocation, List<BarPresentData> bars) {
        for (final BarPresentData data : bars) {
            mRetrofit.create(GoogleDistanceApi.class)
                    .getDistance(lastLocation.latitude + "," + lastLocation.longitude,
                            "place_id:" + data.placeId,
                            "walking",
                            getString(R.string.google_maps_key))
                    .enqueue(new Callback<RowsWrapper<Row>>() {
                        @Override
                        public void onResponse(Call<RowsWrapper<Row>> call, Response<RowsWrapper<Row>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                data.distance = response.body().rows.get(0).elements.get(0).distance.text;
                                EventBus.getDefault().post(new DistanceReceivedEvent());
                            } else {
                                Log.d("DistanceServiceCallback", "Code: " + response.code() + " Message: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<RowsWrapper<Row>> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    public boolean locationPermissionGranted() {
        return mIsLocationPermissionGranted;
    }

}
