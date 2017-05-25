package com.d401f17.TypeSystem;

public class IgnoreType extends Type {
    public IgnoreType() {
    }

    @Override
    public String toString() {
        return "IGNORE";
    }

    @Override
    public String getJavaType() {
        return null;
    }
}
