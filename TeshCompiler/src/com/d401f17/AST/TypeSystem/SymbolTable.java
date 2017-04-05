package com.d401f17.AST.TypeSystem;

import java.util.HashMap;

/**
 * Created by Henrik on 05-04-2017.
 */
public class SymbolTable implements SymTab {
    private SymTab currentTable;

    private HashMap<String, Symbol> table = new HashMap<>();
    private SymTab parentTable;
    private int scopeLevel;

    public SymbolTable(SymTab parentTable, int scopeLevel) {
        this.parentTable = parentTable;
        this.scopeLevel = scopeLevel;
    }

    public void openScope() {
        currentTable = new SymbolTable(this, scopeLevel++);
    }

    public void closeScope() {
        currentTable = this;
        scopeLevel--;
    }

    public int getScopeLevel() {
        return scopeLevel;
    }

    public void insert(String id, Symbol s) {
        table.put(id, s);
    }

    public Symbol lookup(String id) {
        if (table.containsKey(id)) {
            return table.get(id);
        } else {
            return parentTable.lookup(id);
        }
    }
}
