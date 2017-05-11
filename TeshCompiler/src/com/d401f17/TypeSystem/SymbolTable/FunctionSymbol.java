package com.d401f17.TypeSystem.SymbolTable;

import com.d401f17.AST.Nodes.AST;
import com.d401f17.TypeSystem.SymbolTable.Symbol;
import com.d401f17.TypeSystem.SymbolTable.SymbolTable;
import com.d401f17.TypeSystem.Type;

/**
 * Created by mathias on 4/28/17.
 */
public class FunctionSymbol extends Symbol {

    private SymbolTable symbolTable;

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
