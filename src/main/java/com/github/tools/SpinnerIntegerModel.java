package com.github.tools;

/**
 * 
 * @author capdevon
 */
public class SpinnerIntegerModel extends SpinnerModel<Integer> {

    /**
     * Integer implementation
     * 
     * @param minValue
     * @param maxValue
     * @param step
     */
    public SpinnerIntegerModel(Integer minValue, Integer maxValue, Integer step) {
        super(minValue, maxValue, step);
    }

    @Override
    public boolean isWithinRange(Integer value) {
        return value >= getMinValue() && value <= getMaxValue();
    }

}
