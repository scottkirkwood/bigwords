package com.forusers.android.bigwords;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.forusers.android.bigwords.R;

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

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// Don't care
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
				return;
			}
			float pitch = event.values[1];
			float roll = event.values[2];
			if (textView != null) {
				textView.setText("Pitch:" + pitch + " Roll: " + roll);
			}
		}
    };
    
    private TextView textView;
}