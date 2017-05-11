package com.d401f17.TypeSystem;

/**
 * Created by hense on 4/25/17.
 */
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
