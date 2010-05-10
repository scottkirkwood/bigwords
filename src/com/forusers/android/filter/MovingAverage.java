package com.forusers.android.filter;

/**
 * A simple moving average implementation.
 *
 * SMA (Simple moving average) sometimes called rolling average, or running average (mean).
 * see: http://en.wikipedia.org/wiki/Moving_average.
 *
 * @author scottkirkwood
 */
public class MovingAverage implements Filter {

    public MovingAverage(int size) {
        circularBuffer = new float[size];
        reset();
    }

    /**
     * Get the current moving average.
     * @see com.forusers.android.filter.Filter#getValue()
     */
    @Override
    public float getValue() {
        return mean;
    }

    /**
     * @see com.forusers.android.filter.Filter#pushValue(float)
     */
    @Override
    public void pushValue(float x) {
        if (count++ == 0) {
            primeBuffer(x);
        }
        float lastValue = circularBuffer[circularIndex];
        mean = mean + (x - lastValue) / circularBuffer.length;
        circularBuffer[circularIndex] = x;
        circularIndex = nextIndex(circularIndex);
    }

    /*
     * @see com.forusers.android.filter.Filter#reset()
     */
    @Override
    public void reset() {
        count = 0;
        circularIndex = 0;
        mean = 0;
    }

    public long getCount() {
        return count;
    }

    private void primeBuffer(float val) {
        for (int i = 0; i < circularBuffer.length; ++i) {
            circularBuffer[i] = val;
        }
        mean = val;
    }

    private int nextIndex(int curIndex) {
        if (curIndex + 1 >= circularBuffer.length) {
            return 0;
        }
        return curIndex + 1;
    }

    private float circularBuffer[];
    private float mean;
    private int circularIndex;
    private int count;
}
