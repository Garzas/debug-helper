package com.appunite.debughelper.model;

import java.util.List;

public class SelectOption {

    private final int option;
    private int currentPosition;
    private final List<Integer> values;

    public SelectOption(int option, int currentPosition, List<Integer> values) {
        this.option = option;
        this.currentPosition = currentPosition;
        this.values = values;
    }

    public int getOption() {
        return option;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
