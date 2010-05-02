package com.forusers.android.filter;

public interface Filter {
	public void pushValue(float x);
	public void reset();
	public float getValue();
}
