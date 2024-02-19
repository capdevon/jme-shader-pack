package com.github.mat.editor;

/**
 * 
 * @author capdevon
 */
public class SpinnerFloatModel extends SpinnerModel<Float> {

    /**
     * Float implementation
     * 
     * @param minValue
     * @param maxValue
     * @param step
     */
    public SpinnerFloatModel(Float minValue, Float maxValue, Float step) {
        super(minValue, maxValue, step);
    }

    @Override
    public boolean isWithinRange(Float value) {
        return value >= getMinValue() && value <= getMaxValue();
    }
    
}