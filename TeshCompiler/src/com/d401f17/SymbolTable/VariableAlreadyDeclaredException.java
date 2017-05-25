package com.d401f17.SymbolTable;

public class VariableAlreadyDeclaredException extends Exception {
    public VariableAlreadyDeclaredException(String s) {
        super(s);
    }
}
