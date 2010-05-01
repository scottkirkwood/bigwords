package com.forusers.android.bigwords;

import java.awt.font.NumericShaper;
import java.util.Formatter;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.forusers.android.bigwords.R;
import com.forusers.android.filter.AccelerometerFilter;

public class BigWords extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SensorManager sm;
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
         
        sm.registerListener(sl, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
        		SensorManager.SENSOR_DELAY_NORMAL, new Handler());
        textView = (TextView) findViewById(R.id.text);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    private final SensorEventListener sl = new SensorEventListener() {
    	final AccelerometerFilter rollFilter = new AccelerometerFilter(50, 6);
    	
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// Don't care
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
				return;
			}
			float roll = event.values[2];
			rollFilter.pushValue(roll);
			if (textView != null) {
				textView.setText("Roll: " + String.format("%.0f", rollFilter.getValue()));
			}
		}
    };
    
    private TextView textView;
}