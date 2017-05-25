package com.d401f17.Visitors.Interpreter;


import com.d401f17.SymbolTable.SymbolTable;

public class Environment {
    private SymbolTable variableTable;
    private SymbolTable recordTable;

    public Environment(SymbolTable variableTable, SymbolTable recordTable) {
        this.variableTable = variableTable;
        this.recordTable = recordTable;
    }

    public Environment() {
        this.variableTable = new SymbolTable();
        this.recordTable = new SymbolTable();
    }

    public SymbolTable getVariableTable() {

        return variableTable;
    }

    public void setVariableTable(SymbolTable variableTable) {
        this.variableTable = variableTable;
    }

    public SymbolTable getRecordTable() {
        return recordTable;
    }

    public void setRecordTable(SymbolTable recordTable) {
        this.recordTable = recordTable;
    }

    public void openScope() {
        variableTable.openScope();
        recordTable.openScope();
    }

    public void closeScope() {
        variableTable.closeScope();
        recordTable.closeScope();
    }
}
