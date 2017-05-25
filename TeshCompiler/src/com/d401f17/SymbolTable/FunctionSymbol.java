package com.d401f17.SymbolTable;

import com.d401f17.AST.Nodes.AST;
import com.d401f17.TypeSystem.Type;

public class FunctionSymbol extends Symbol {

    private SymbolTable symbolTable;
    private String methodName;

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public FunctionSymbol(Type type, AST declarationNode, SymbolTable symbolTable) {
        super(type, declarationNode);
        this.symbolTable = symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
