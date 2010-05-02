package com.forusers.android;

import com.forusers.android.filter.LowPassFilter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class OrientationListener implements SensorEventListener {
	private static float RESET_ANGLE = 999;
	private static int SETTLE_COUNT = 10;  // How long for the filter to settle.
	private LowPassFilter rollFilter = new LowPassFilter(50, 25);
	private float startAngle = RESET_ANGLE;
	
	public OrientationListener() {
		reset();
	}
	
	public void reset() {
		startAngle = RESET_ANGLE;
		rollFilter.reset();
	}

	public float deltaAngle() {
		if (startAngle == RESET_ANGLE) {
			return 0;
		}
		return rollFilter.getValue() - startAngle;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
			return;
		}
		float roll = event.values[2];
		rollFilter.pushValue(roll);
		if (startAngle == RESET_ANGLE && rollFilter.getCount() == SETTLE_COUNT) {
			startAngle = rollFilter.getValue();
		}
	}
}
