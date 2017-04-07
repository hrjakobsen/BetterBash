package com.d401f17.AST.TypeSystem;

import java.util.HashMap;

/**
 * Created by Henrik on 05-04-2017.
 */
public class SymbolTable implements SymTab {
    private SymbolTable currentTable = this;
    private SymbolTable parentTable = null;

    private HashMap<String, Symbol> table = new HashMap<>();

    public SymbolTable getParentTable() {
        return parentTable;
    }


    public HashMap<String, Symbol> getTable() {
        return table;
    }

    public void setTable(HashMap<String, Symbol> table) {
        this.table = table;
    }

    public SymbolTable() {
    }

    public void setParentTable(SymbolTable parentTable) {
        this.parentTable = parentTable;
    }

    public void openScope() {
        SymbolTable newTable = new SymbolTable();
        newTable.setParentTable(currentTable);
        currentTable = newTable;
    }

    public void closeScope() {
        currentTable = currentTable.getParentTable();
    }

    public void insert(String id, Symbol s) {
        currentTable.getTable().put(id, s);
    }

    public Symbol lookup(String id) throws VariableNotDeclaredException {
        if (table.containsKey(id)) {
            return table.get(id);
        } else {
            if (parentTable == null) {
                throw new VariableNotDeclaredException("Variable " + id + " not declared");
            } else {
                return parentTable.lookup(id);
            }
        }
    }
}
