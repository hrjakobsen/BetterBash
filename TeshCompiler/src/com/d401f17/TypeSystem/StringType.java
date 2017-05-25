package com.d401f17.TypeSystem;

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
