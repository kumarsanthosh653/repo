package com.ozonetel.occ.service.impl;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 *
 * @author pavanj
 */
public class MyExclusionStrategy implements ExclusionStrategy {

    private final Class<?> typeToSkip;

    public MyExclusionStrategy(Class<?> typeToSkip) {
        this.typeToSkip = typeToSkip;
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz == typeToSkip);
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return false;
    }
}
