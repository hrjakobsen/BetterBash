package com.d401f17.TypeSystem;

/**
 * Created by hense on 4/25/17.
 */
public class StringType extends Type {
    public StringType() {
    }

    @Override
    public String toString() {
        return "STRING";
    }

    @Override
    public String getJavaType() {
        return "Ljava/lang/String;";
    }
}
