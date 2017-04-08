package com.d401f17.AST.TypeSystem;

/**
 * Created by Henrik on 05-04-2017.
 */
public interface SymTab {
    public void openScope();
    public void closeScope();

    public void insert(String id, Symbol s) throws VariableAlreadyDeclaredException;
    public Symbol lookup(String id) throws VariableNotDeclaredException;
}
