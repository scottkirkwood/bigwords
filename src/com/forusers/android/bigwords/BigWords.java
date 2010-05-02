package com.forusers.android.bigwords;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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

import com.forusers.android.OrientationListener;
import com.forusers.android.filter.LowPassFilter;

public class BigWords extends Activity implements OnClickListener {
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
    public void onResume() {
		super.onResume();
		setWpmText(wordsPerMinute, 0);
    }

	@Override
    public void onPause() {
		super.onPause();
		stopPlaying();
    }

    private void onPlayOrPause() {
    	if (paused.get()) {
    		startPlaying();
    	} else {
    		stopPlaying();
    	}
	}
    
	private void startPlaying() {
		Log.i(TAG, "Start Playing");
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
		Log.i(TAG, "Stop Playing");
		if (!paused.compareAndSet(false, true)) {
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

	private void setWpmText(int wpm, int diff) {
		TextView t = (TextView) findViewById(R.id.wpm);
		String format = getString(R.string.wpm_format);
		t.setText(String.format("%+d %d wpm", diff, wpm));
	}
	
	private void startListening() {
		orientationSensor.reset();
		
	    SensorManager sm;
	    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	     
	    sm.registerListener(orientationSensor, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION),
	    		SensorManager.SENSOR_DELAY_NORMAL, new Handler());
	}
	
	private void stopListening() {
	    SensorManager sm;
	    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	    sm.unregisterListener(orientationSensor);	    	
	}
	
    private final OrientationListener orientationSensor = new OrientationListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			super.onSensorChanged(event);
			
			float diff = rollFilter.getValue() - startAngle.getValue();
			if (Math.abs(diff) > 20) {
				stopPlaying();
			} else if (diff < 10) {
				wordsPerMinute++;
				setWpmText(wordsPerMinute, +1);
			} else if (diff > 10) {
				wordsPerMinute--;
				setWpmText(wordsPerMinute, -1);
			}
		}
    };
 
    private final Runnable nextWordTask = new Runnable() {
		@Override
		public void run() {
			if (textView == null) {
				Log.e(TAG, "Missing textview in nextWordTask");
				return;
			}
			textView.setText(String.format("Word%03d", wordIndex++));			
			timerHandler.postDelayed(this, wpmInDelayMillis());
		}
    };
    
    private static final String TAG = "BigWords";
    private TextView textView;
    private PowerManager.WakeLock wakeLock;
    private final Handler timerHandler = new Handler();
    private AtomicBoolean paused = new AtomicBoolean(true);
    private int wordsPerMinute = 150;
    private long wordIndex = 0;
}