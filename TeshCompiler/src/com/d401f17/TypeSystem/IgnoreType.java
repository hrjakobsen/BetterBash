package com.d401f17.TypeSystem;

/**
 * Created by hense on 4/25/17.
 */
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
