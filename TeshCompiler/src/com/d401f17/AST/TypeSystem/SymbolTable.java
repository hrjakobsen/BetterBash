package com.d401f17.AST.TypeSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Henrik on 05-04-2017.
 */
public class SymbolTable implements SymTab {
    private ArrayList<HashMap<String, Symbol>> tables = new ArrayList<>();
    private int scopeLevel = 0;


    public SymbolTable() {
    }

    public void openScope() {
        scopeLevel++;
        tables.get(scopeLevel) = new HashMap<>();
    }

    public void closeScope() {

        currentTable = currentTable.getParentTable();
    }

    public void insert(String id, Symbol s) throws VariableAlreadyDeclaredException {
        if (currentTable.getTable().containsKey(id)) {
            throw new VariableAlreadyDeclaredException("Variable " + id + " already declared in this scope");
        } else {
            currentTable.getTable().put(id, s);
        }
    }

    public Symbol lookup(String id) throws VariableNotDeclaredException {

        if (table.containsKey(id)) {
            return table.get(id);
        } else {
            if (currentTable.getParentTable() == null) {
                throw new VariableNotDeclaredException("Variable " + id + " not declared");
            } else {
                //System.out.println(currentTable.getParentTable().getTable().keySet());
                return currentTable.lookup(id);
            }
        }
    }
}
