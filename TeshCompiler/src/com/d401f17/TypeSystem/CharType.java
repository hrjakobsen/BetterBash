package com.d401f17.TypeSystem;

public class CharType extends Type {
    public CharType() {
    }

    @Override
    public String toString() {
        return "CHAR";
    }

    @Override
    public String getJavaType() {
        return "Ljava/lang/String;";
    }
}
