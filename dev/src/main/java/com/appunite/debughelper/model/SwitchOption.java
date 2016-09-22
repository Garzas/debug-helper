package com.appunite.debughelper.model;

public class SwitchOption {

    private final boolean set;
    private final int option;

    public SwitchOption(boolean set, int option) {
        this.set = set;
        this.option = option;
    }

    public boolean isSet() {
        return set;
    }

    public int getOption() {
        return option;
    }
}
