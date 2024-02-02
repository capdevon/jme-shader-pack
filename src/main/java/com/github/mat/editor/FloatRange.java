package com.github.mat.editor;

/**
 * 
 * @author capdevon
 */
public class FloatRange {

    final float min;
    final float max;
    final float step;

    public FloatRange(float min, float max, float step) {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

}