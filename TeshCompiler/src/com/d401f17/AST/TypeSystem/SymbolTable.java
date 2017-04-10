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
        tables.add(new HashMap<>());
    }

    public void openScope() {
        scopeLevel++;
        while (tables.size() <= scopeLevel) tables.add(null);
        tables.set(scopeLevel, new HashMap<>());
    }

    public void closeScope() {
        scopeLevel--;
    }

    public void insert(String id, Symbol s) throws VariableAlreadyDeclaredException {
        if (tables.get(scopeLevel).containsKey(id)) {
            throw new VariableAlreadyDeclaredException("Variable " + id + " already declared in this scope");
        } else {
            tables.get(scopeLevel).put(id, s);
        }
    }

    public Symbol lookup(String id) throws VariableNotDeclaredException {
        int i = scopeLevel;
        while (i >= 0) {
            if (tables.get(i).containsKey(id)) {
                return tables.get(i).get(id);
            }
            i--;
        }
        throw new VariableNotDeclaredException("Variable " + id + " not declared");
    }
}