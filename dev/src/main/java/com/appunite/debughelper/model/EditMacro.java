package com.appunite.debughelper.model;

import javax.annotation.Nonnull;

public class EditMacro {

    private final int position;
    @Nonnull
    private final String name;

    public EditMacro(final int position, @Nonnull final String name) {
        this.position = position;
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    @Nonnull
    public String getName() {
        return name;
    }
}
