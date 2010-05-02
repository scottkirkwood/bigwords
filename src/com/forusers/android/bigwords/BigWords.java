package com.forusers.android.bigwords;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.forusers.android.filter.AccelerometerFilter;

public class BigWords extends Activity implements OnClickListener {
    private static final int INVALID_ANGLE = 999;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "DoNotDimScreen");

        textView = (TextView) findViewById(R.id.text);
        
        Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View v) {
		if (R.id.play == v.getId()) {
			onPlayOrPause();
		}
	}
	
	@Override
    public void onPause() {
		super.onPause();
		stopPlaying();
    }

	@Override
    public void onResume() {
		super.onResume();
    }

    private void onPlayOrPause() {
    	if (paused.get()) {
    		startPlaying();
    	} else {
    		stopPlaying();
    	}
	}
    
	private void startPlaying() {
		if (!paused.compareAndSet(true, false)) {
			Log.i(TAG, "Attempted to start playing when already playing.");
			return;
		}
		Button b = (Button) findViewById(R.id.play);
		b.setText(R.string.stop);
		
		startTimer();
		startListening();
		wakeLock.acquire();
	}

	private void stopPlaying() {
		if (paused.compareAndSet(false, true)) {
			Log.i(TAG, "Attempted to stop playing when already stopped.");
			return;
		}
		Button b = (Button) findViewById(R.id.play);
		b.setText(R.string.play);
		
		stopTimer();
		stopListening();
		wakeLock.release();
	}
	
	private void startTimer() {
		timerHandler.removeCallbacks(nextWordTask);  // Just in case
		timerHandler.postDelayed(nextWordTask, wpmInDelayMillis());
	}

	private long wpmInDelayMillis() {
		return 60000 / wordsPerMinute;
	}

	private void stopTimer() {
		timerHandler.removeCallbacks(nextWordTask);
	}

	private void startListening() {
	    SensorManager sm;
	    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	     
	    sm.registerListener(orientationSensor, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
	    		SensorManager.SENSOR_DELAY_NORMAL, new Handler());
	    startAngle = INVALID_ANGLE;
	}
	
	private void stopListening() {
	    SensorManager sm;
	    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	    sm.unregisterListener(orientationSensor);	    	
	}
	
    private final SensorEventListener orientationSensor = new SensorEventListener() {
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
			float diff = rollFilter.getValue() - startAngle;
		}
    };
 
    private final Runnable nextWordTask = new Runnable() {
		@Override
		public void run() {
			if (textView == null) {
				Log.e(TAG, "Missing textview in nextWordTask");
				return;
			}
			textView.setText(String.format("Word%03d", wordIndex));			
			timerHandler.postDelayed(this, wpmInDelayMillis());
		}
    };
    
    private static final String TAG = "BigWords";
    private TextView textView;
    private PowerManager.WakeLock wakeLock;
    private final Handler timerHandler = new Handler();
    private AtomicBoolean paused = new AtomicBoolean(true);
    private float startAngle = INVALID_ANGLE;
    private int wordsPerMinute = 200;
    private long wordIndex = 0;
}