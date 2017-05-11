package com.d401f17.TypeSystem.SymbolTable;

/**
 * Created by hense on 4/5/17.
 */
public class VariableNotDeclaredException extends Exception {
    public VariableNotDeclaredException(String s) {
        super(s);
    }
}
