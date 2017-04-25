package com.d401f17.TypeSystem;

/**
 * Created by hense on 4/25/17.
 */
public class ErrorType extends Type {
    public ErrorType() {
    }

    public ErrorType(int lineNum, String errorMessage) {
        super(lineNum, errorMessage);
    }
}
