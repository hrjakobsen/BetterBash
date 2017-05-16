package com.d401f17.TypeSystem;

/**
 * Created by hense on 4/5/17.
 */
public class Type implements Comparable<Type> {
    private int lineNum;
    private String errorMessage;
    private boolean immutable;

    public Type() {}

    public Type(int lineNum, String errorMessage) {
        this.lineNum = lineNum;
        this.errorMessage = errorMessage;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getErrorMessage() {
        return "Error on line " + lineNum + ": " + errorMessage;
    }

    public boolean isImmutable() {
        return immutable;
    }

    public void setImmutable(boolean immutable) {
        this.immutable = immutable;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Type && this.getClass().equals(o.getClass());
    }

    @Override
    public int compareTo(Type o) {
        return lineNum - o.lineNum;
    }
}
