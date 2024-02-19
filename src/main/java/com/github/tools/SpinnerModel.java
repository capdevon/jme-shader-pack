package com.github.tools;

/**
 * @author capdevon
 */
public abstract class SpinnerModel<T extends Number> {

    private T minValue;
    private T maxValue;
    private T step;

    /**
     * 
     * @param minValue
     * @param maxValue
     * @param step
     */
    public SpinnerModel(T minValue, T maxValue, T step) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    public abstract boolean isWithinRange(T value);

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }
    
    public T getStep() {
        return step;
    }
    
}