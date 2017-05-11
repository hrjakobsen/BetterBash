package com.d401f17.TypeSystem.SymbolTable;

/**
 * Created by hense on 4/8/17.
 */
public class VariableAlreadyDeclaredException extends Exception {
    public VariableAlreadyDeclaredException(String s) {
        super(s);
    }
}
