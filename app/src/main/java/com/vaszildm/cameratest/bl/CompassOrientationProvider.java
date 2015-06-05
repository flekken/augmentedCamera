package com.vaszildm.cameratest.bl;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

public class CompassOrientationProvider implements SensorEventListener {

    private static final int DEGREES_360 = 360;
    private final SensorManager mSensorManager;
    private float mAzimuth;
    private IOrientationListener mListener;

    public CompassOrientationProvider(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startOrientationProvider(IOrientationListener listener)
    {
        mListener = listener;
        final Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (sensor != null) {
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stopOrientationProvider()
    {
        mListener = null;
        mSensorManager.unregisterListener(this);
    }

    public float getLastKnownOrientation()
    {
        return mAzimuth;
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy){}

    @Override
    public void onSensorChanged(final SensorEvent event){
        float newOrientation = getRotation(event,GpsLocationProvider.getInstance(null).getLastKnownLocation());
        mListener.orientationChanged(newOrientation);
    }

    private float getRotation(final SensorEvent event, Location lastKnownLocation) {

        GeomagneticField geomagneticField = new GeomagneticField(
                (float) lastKnownLocation.getLatitude(),
                (float) lastKnownLocation.getLongitude(),
                (float) lastKnownLocation.getAltitude(),
                System.currentTimeMillis());

        mAzimuth = event.values[0];
        mAzimuth -= geomagneticField.getDeclination();

        float rotation = mAzimuth;

        if (rotation < 0) {
            rotation = rotation + DEGREES_360;
        }

        return rotation;
    }
}
