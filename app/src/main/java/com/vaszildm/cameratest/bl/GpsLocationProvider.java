package com.vaszildm.cameratest.bl;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsLocationProvider implements LocationListener {

    private final LocationManager mLocationManager;

    private Location mLocation;

    private long mLocationUpdateMinTime;
    private float mLocationUpdateMinDistance;

    private static GpsLocationProvider mInstance;

    public static GpsLocationProvider getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GpsLocationProvider(context);
        }

        return mInstance;
    }

    public void startLocationProvider() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mLocationUpdateMinTime,
                mLocationUpdateMinDistance, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mLocationUpdateMinTime,
                mLocationUpdateMinDistance, this);
    }

    public void stopLocationProvider() {
        mLocationManager.removeUpdates(this);
    }

    public GpsLocationProvider(Context context) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public long getLocationUpdateMinTime() {
        return mLocationUpdateMinTime;
    }

    public void setLocationUpdateMinTime(final long milliSeconds) {
        mLocationUpdateMinTime = milliSeconds;
    }

    public float getLocationUpdateMinDistance() {
        return mLocationUpdateMinDistance;
    }

    public void setLocationUpdateMinDistance(final float meters) {
        mLocationUpdateMinDistance = meters;
    }

    public Location getLastKnownLocation() {
        return (mLocation == null) ? mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) : mLocation;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if  (isBetterLocation(location, mLocation)) {
            mLocation = location;
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 60000;
        boolean isSignificantlyOlder = timeDelta < -60000;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }
    @Override
    public void onProviderEnabled(final String provider) {
    }
    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }
}
