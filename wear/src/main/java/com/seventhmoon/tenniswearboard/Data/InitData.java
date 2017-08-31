package com.seventhmoon.tenniswearboard.Data;

import android.hardware.Sensor;
//import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.common.api.GoogleApiClient;

public class InitData {
    public SensorManager mSensorManager;
    //public Sensor mAccelerometer;
    //public Sensor mGravity;
    //public Sensor mGyroscope;
    //public Sensor mGyroscope_uncalibrated;
    //public Sensor mLinearAcceration;
    //public Sensor mRotationVector;
    public Sensor mStepCounter;
    public boolean is_running;
    //public SensorEventListener accelerometerListener;
    //public static SensorEventListener linearaccelerometerListener;
    //public SensorEventListener rotationVectorListener;
    //public SensorEventListener stepCountListener;

    public boolean is_voice_enable = false;

    public boolean is_debug = false;

    public GoogleApiClient mGoogleApiClient;
}
