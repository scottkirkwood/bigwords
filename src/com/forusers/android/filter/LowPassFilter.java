package com.forusers.android.filter;

public class LowPassFilter implements Filter {
	/**
	 * See http://en.wikipedia.org/wiki/Low-pass_filter.
	 */
    private float alpha;
    private float value;
    private long count;
    
    // Fast sample rate is about 50Hz, good cutoff is about 6Hz
    public LowPassFilter(float sampleRate, float cutoffFrequency) {
    	float dt = (float) (1.0 / sampleRate);
    	float RC = (float) (1.0 / cutoffFrequency);
    	alpha = dt / (RC + dt);
    	reset();
    }
    
    @Override
    public void reset() {
    	count = 0;
    	value = 0;
    }
    
    @Override
	public void pushValue(float x) {
		if (count++ == 0) {
			value = x;
			return;
		}
		value = value + alpha * (x  - value);
	}
	
    @Override
	public float getValue() {
		return value;
	}
	
	public long getCount() {
		return count;
	}
	
	public float getAlpha() {
		return alpha;
	}
}
