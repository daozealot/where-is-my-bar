package com.sample.mybar.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sample.mybar.R;
import com.sample.mybar.utils.common.BarPresentData;
import com.sample.mybar.events.BarsReceivedEvent;
import com.sample.mybar.events.map.UpdateCameraEvent;
import com.sample.mybar.events.map.UpdateUiEvent;
import com.sample.mybar.ui.PermissionCheck;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int DEFAULT_ZOOM = 15;

    // Default location is Sofia center
    public static final LatLng MAP_DEFAULT_LAT_LNG = new LatLng(42.697032, 23.321040);

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private CameraPosition mCameraPosition;

    private MapView mMapView;
    private GoogleMap mMap;
    private PermissionCheck mPermissionCheck;
    private OnMarkerClickListener mMarkerClickListener;

    public MapFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mPermissionCheck = (PermissionCheck) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PermissionCheck");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = v.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return v;
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
        }

        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAP_DEFAULT_LAT_LNG, DEFAULT_ZOOM));

        updateLocationUi();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateCameraEvent e) {
        if (mMap == null) {
            return;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(e.latLng.latitude,
                        e.latLng.longitude), DEFAULT_ZOOM));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnMessageEvent(BarsReceivedEvent e) {
        for (BarPresentData data : e.barsData) {
            addMarker(data);
        }
    }

    public void addMarker(BarPresentData data) {
        if (mMap == null) {
            return;
        }

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(data.location.lat, data.location.lng))
                .title(data.name))
                .setTag(data);

        if (mMarkerClickListener == null) {
            // Set a listener for marker click.
            mMap.setOnMarkerClickListener(getMarkerClickListener());
        }
    }

    private OnMarkerClickListener getMarkerClickListener() {
        mMarkerClickListener = new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (marker.getTag() != null) {
                    BarPresentData barData = (BarPresentData) marker.getTag();
                    marker.setSnippet(
                            String.format(
                                    getString(R.string.distance_format),
                                    barData.distance));
                }

                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            }
        };
        return mMarkerClickListener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateUiEvent e) {
        updateLocationUi();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUi() {
        if (mMap == null) {
            return;
        }
        try {
            if (mPermissionCheck.locationPermissionGranted()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}

