package com.forusers.android;

import com.forusers.android.filter.Filter;
import com.forusers.android.filter.LowPassFilter;
import com.forusers.android.filter.MovingAverage;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class OrientationListener implements SensorEventListener {
    private static int SETTLE_COUNT = 10;  // How long for the filter to settle.
    private Filter rollFilter = new LowPassFilter(50, 25);
    private Filter startAngle = new MovingAverage(100); 
    
    public OrientationListener() {
        reset();
    }
    
    public void reset() {
        startAngle.reset();
        rollFilter.reset();
    }

    public float deltaAngle() {
        if (startAngle.getCount() == 0) {
            return 0;
        }
        return rollFilter.getValue() - startAngle.getValue();
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // don't care.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
            return;
        }
        float roll = event.values[2];
        rollFilter.pushValue(roll);
        if (startAngle.getCount() == 0 && rollFilter.getCount() == SETTLE_COUNT) {
            startAngle.pushValue(rollFilter.getValue());
        }
    }
}
