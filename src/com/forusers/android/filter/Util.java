package com.forusers.android.filter;

public class Util {
    private Util() {}

    public static double Norm(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public static double Clamp(double v, double min, double max) {
        if(v > max)
            return max;
        else if(v < min)
            return min;
        else
            return v;
    }
}
