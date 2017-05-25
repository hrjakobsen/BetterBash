package com.d401f17.TypeSystem;

public class BoolType extends Type {
    public BoolType() {
    }

    @Override
    public String toString() {
        return "BOOL";
    }

    @Override
    public String getJavaType() {
        return "I";
    }
}
