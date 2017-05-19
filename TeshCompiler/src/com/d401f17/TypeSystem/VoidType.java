package com.d401f17.TypeSystem;

/**
 * Created by hense on 4/25/17.
 */
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
