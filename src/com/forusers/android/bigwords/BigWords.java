package com.forusers.android.bigwords;

import java.util.concurrent.atomic.AtomicBoolean;

import com.forusers.android.ChangeIndicator;
import com.forusers.android.HorizontalProgressBar;
import com.forusers.android.worditerator.ESTWordIteratorImpl;
import com.forusers.android.worditerator.Word;
import com.forusers.android.worditerator.WordIterator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.forusers.android.OrientationListener;
import com.forusers.android.ValueWithUpdateFrequency;

public class BigWords extends Activity implements OnClickListener {
    private static final int MAX_MOVE_ANGLE = 30;
    private static final int MIN_MOVE_ANGLE = 3;

    private static final String TAG = "BigWords";
    private static final String PREFS = "BigWords";
    private TextView textView;
    private PowerManager.WakeLock wakeLock;
    private final Handler timerHandler = new Handler();
    private AtomicBoolean paused = new AtomicBoolean(true);
    private ValueWithUpdateFrequency wordsPerMinute;
    private Vibrator vibrator;
    private WordIterator wordIter;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        loadPrefs();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "DoNotDimScreen");

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        textView = (TextView) findViewById(R.id.text);
        textView.setOnClickListener(this);

        Button play = (Button) findViewById(R.id.play);
        play.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        savePrefs();
    }

    private void loadPrefs() {
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        wordsPerMinute = new ValueWithUpdateFrequency(settings.getInt("wpm", 200), 250);
    }

    private void savePrefs() {
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("wpm", wordsPerMinute.getValue());
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        if (R.id.play == v.getId()) {
            onPlayOrPause();
        } else if (R.id.text == v.getId()) {
            if (!paused.get()) {
                stopPlaying();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        wordIter = new ESTWordIteratorImpl();
        wordIter.open("");
        setWpmText(wordsPerMinute.getValue(), 0);
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
        if (!paused.compareAndSet(true, false)) {
            Log.i(TAG, "Attempted to start playing when already playing.");
            return;
        }
        startListening();

        Button b = (Button) findViewById(R.id.play);
        b.setText(R.string.stop);

        vibrator.vibrate(50);
        startTimer();
        wakeLock.acquire();
    }

    private void stopPlaying() {
        if (!paused.compareAndSet(false, true)) {
            Log.i(TAG, "Attempted to stop playing when already stopped.");
            return;
        }
        Button b = (Button) findViewById(R.id.play);
        b.setText(R.string.play);

        vibrator.vibrate(50);
        stopTimer();
        stopListening();
        wakeLock.release();
    }

    private void startTimer() {
        timerHandler.removeCallbacks(nextWordTask);  // Just in case
        timerHandler.postDelayed(nextWordTask, wpmInDelayMillis());
    }

    private long wpmInDelayMillis() {
        return 60000 / wordsPerMinute.getValue();
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(nextWordTask);
    }

    private void setWpmText(int wpm, float diff) {
        HorizontalProgressBar p = (HorizontalProgressBar) findViewById(R.id.wpm);
        p.setPosition(wpm);

        ChangeIndicator ci = (ChangeIndicator) findViewById(R.id.tilt);
        ci.setPosition((int) diff * 20);
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

            float diff = deltaAngle();
            if (Math.abs(diff) > MAX_MOVE_ANGLE) {
                stopPlaying();
            } else if (diff < -MIN_MOVE_ANGLE) {
                wordsPerMinute.Increment((int) -diff);
                setWpmText(wordsPerMinute.getValue(), -diff);
            } else if (diff > MIN_MOVE_ANGLE) {
                wordsPerMinute.Increment((int) -diff);
                setWpmText(wordsPerMinute.getValue(), -diff);
            } else if (Math.abs(diff) < MIN_MOVE_ANGLE) {
                setWpmText(wordsPerMinute.getValue(), 0);
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
            Word word = wordIter.next();
            if (word == null) {
                textView.setText("Fin!");
            } else {
                textView.setText(word.getWord());
            }

            timerHandler.postDelayed(this, wpmInDelayMillis());
        }
    };
}
