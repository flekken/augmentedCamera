package com.vaszildm.cameratest.ui;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vaszildm.cameratest.R;
import com.vaszildm.cameratest.bl.CompassOrientationProvider;
import com.vaszildm.cameratest.bl.GpsLocationProvider;
import com.vaszildm.cameratest.bl.IOrientationListener;


public class MapFragment extends Fragment implements IOrientationListener {

    private static final String TAG = "MapFragment";

    private MainActivity mMainActivity;

    private GpsLocationProvider mGpsLocationProvider;
    private CompassOrientationProvider mCompassOrientationProvider;
    private float thetaV;
    private float thetaH;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Camera.Parameters p = CameraFragment.getCameraInstance().getParameters();
        thetaV = p.getVerticalViewAngle();
        thetaH = p.getHorizontalViewAngle();

        Log.d(TAG, "thetaV: " + p.getVerticalViewAngle());
        Log.d(TAG,"thetaH: " + p.getHorizontalViewAngle());

        mGpsLocationProvider = GpsLocationProvider.getInstance(mMainActivity);
        mCompassOrientationProvider = new CompassOrientationProvider(mMainActivity);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mGpsLocationProvider.startLocationProvider();
        mCompassOrientationProvider.startOrientationProvider(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mGpsLocationProvider.stopLocationProvider();
        mCompassOrientationProvider.stopOrientationProvider();
    }

    @Override
    public void orientationChanged(float newOrientation) {
        float from = newOrientation - (thetaH/2);
        float to = newOrientation + (thetaH/2);
    }
}
