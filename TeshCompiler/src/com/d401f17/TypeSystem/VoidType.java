package com.d401f17.TypeSystem;

public class VoidType extends Type {
    public VoidType() {
    }

    @Override
    public String toString() {
        return "VOID";
    }

    @Override
    public String getJavaType() {
        return "V";
    }
}
