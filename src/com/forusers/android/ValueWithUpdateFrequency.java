package com.forusers.android;

import android.os.SystemClock;

/**
 * Create a variable that can only get changed periodically.
 * Should be a template, but I've embedded the value for speed/memory.
 * @author scottkirkwood
 * @param <T>
 */
public class ValueWithUpdateFrequency {
    private int value;
    private long lastUpdate;
    private int msDelay;

    public ValueWithUpdateFrequency(int startValue, int msBetweenUpdates) {
        value = startValue;
        msDelay = msBetweenUpdates;
        lastUpdate = SystemClock.uptimeMillis();
    }

    /**
     * Gets the last value set.
     */
    public int getValue() {
        return value;
    }

    public boolean Increment(int byWhat) {
        return updateValue(value + byWhat);
    }

    /**
     * Updates the value if enough time has elapsed since the last update.
     *
     * @param newValue
     * @return True if the value was changed, false otherwise.
     */
    public boolean updateValue(int newValue) {
        long curTime = SystemClock.uptimeMillis();
        if (curTime - lastUpdate > msDelay) {
            value = newValue;
            lastUpdate = curTime;
            return true;
        }
        return false;
    }
}
