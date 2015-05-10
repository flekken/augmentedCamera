package com.vaszildm.cameratest.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.vaszildm.cameratest.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.main_fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            CameraFragment cameraFragment = new CameraFragment();

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.main_fragment_container, cameraFragment).commit();
        }

    }
}
