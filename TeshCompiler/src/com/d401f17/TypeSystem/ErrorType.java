package com.d401f17.TypeSystem;

public class ErrorType extends Type {
    public ErrorType() {
    }

    public ErrorType(int lineNum, String errorMessage) {
        super(lineNum, errorMessage);
    }

    @Override
    public String toString() {
        return "ERROR";
    }

    @Override
    public String getJavaType() {
        return null;
    }
}
