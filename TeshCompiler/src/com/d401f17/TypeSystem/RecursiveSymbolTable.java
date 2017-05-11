package com.d401f17.TypeSystem;

import com.d401f17.TypeSystem.SymbolTable.SymTab;
import com.d401f17.TypeSystem.SymbolTable.Symbol;
import com.d401f17.TypeSystem.SymbolTable.VariableAlreadyDeclaredException;
import com.d401f17.TypeSystem.SymbolTable.VariableNotDeclaredException;

import java.util.HashMap;

/**
 * Created by mathias on 4/28/17.
 */
public class RecursiveSymbolTable implements SymTab {
    private RecursiveSymbolTable parent = null;
    private RecursiveSymbolTable currentTable = this;
    private HashMap<String, Symbol> entries = new HashMap<>();

    public RecursiveSymbolTable(RecursiveSymbolTable parent) {
        this.parent = parent;
    }

    public RecursiveSymbolTable(String ... reservedWords) {
        for (String word : reservedWords) {
            entries.put(word, null);
        }
    }

    public RecursiveSymbolTable() {
    }

    @Override
    public void openScope() {
        currentTable = new RecursiveSymbolTable(this);
    }

    @Override
    public void closeScope() {
        currentTable = currentTable.parent;
    }

    @Override
    public void insert(String id, Symbol s) throws VariableAlreadyDeclaredException {
        if (currentTable.entries.containsKey(id)) {
            throw new VariableAlreadyDeclaredException(id);
        }
        currentTable.entries.put(id, s);
    }

    @Override
    public Symbol lookup(String id) throws VariableNotDeclaredException {
        return currentTable.recursiveLookup(id);
    }

    private Symbol recursiveLookup(String id) throws VariableNotDeclaredException {
        if (entries.containsKey(id)) {
            return entries.get(id);
        }
        if (parent == null) {
            throw new VariableNotDeclaredException(id);
        }
        return parent.recursiveLookup(id);
    }
}
