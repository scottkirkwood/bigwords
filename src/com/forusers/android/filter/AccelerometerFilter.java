package com.forusers.android.filter;

public class AccelerometerFilter implements Filter {
    public static float ACCELEROMETER_MIN_STEP = (float) 0.02;
    public static float ACCELEROMETER_NOISE_ATTENUATION = (float) 3.0;
    
    private float alpha;
    private float value;
    
    // Fast sample rate is about 50Hz, good cutoff is about 75Hz
    AccelerometerFilter(float sampleRate, float cutoffFrequency) {
    	float dt = (float) (1.0 / sampleRate);
    	float RC = (float) (1.0 / cutoffFrequency);
    	alpha = dt / (RC + dt);
    }
	public void pushValue(float x) {
		value = value + alpha * (x  - value);
	}
	
	public float getValue() {
		return value;
	}
}
