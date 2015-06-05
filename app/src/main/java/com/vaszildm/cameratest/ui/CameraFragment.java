package com.vaszildm.cameratest.ui;


import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.vaszildm.cameratest.R;

public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private static Camera mCamera;
    private CameraPreview mCameraPreview;
    private MainActivity mMainActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.camera_preview);
        mCameraPreview = new CameraPreview(mMainActivity, surfaceView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraPreview.startPreview(CameraFragment.getCameraInstance());
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraFragment.stopCamera();
        mCameraPreview.stopPreview();
    }

    public static void stopCamera() {
        if (mCamera != null) {
            mCamera.startPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public static Camera getCameraInstance(){
        if (mCamera == null) {
            try {
                mCamera = Camera.open(); // attempt to get a Camera instance
            }
            catch (Exception e){
                Log.e(TAG, "Failed to open camera.", e);
            }
        }
        return mCamera;
    }
}
