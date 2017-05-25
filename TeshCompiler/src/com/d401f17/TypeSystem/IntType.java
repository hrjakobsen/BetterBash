package com.d401f17.TypeSystem;

public class IntType extends FloatType {
    public IntType() {
    }

    @Override
    public String toString() {
        return "INT";
    }

    @Override
    public String getJavaType() {
        return "J";
    }
}
