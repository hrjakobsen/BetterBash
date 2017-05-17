package com.d401f17.TypeSystem;

import com.d401f17.AST.Nodes.VariableDeclarationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mathias on 4/28/17.
 */
public class RecursiveSymbolTable<T> {
    private RecursiveSymbolTable<T> parent = null;
    private RecursiveSymbolTable<T> currentTable = this;
    private HashMap<String, T> entries = new HashMap<>();

    public RecursiveSymbolTable(RecursiveSymbolTable<T> parent) {
        this.parent = parent;
    }

    public RecursiveSymbolTable(String ... reservedWords) {
        for (String word : reservedWords) {
            entries.put(word, null);
        }
    }

    public RecursiveSymbolTable() {
    }

    public void openScope() {
        currentTable = new RecursiveSymbolTable<T>(this);
    }

    public void closeScope() {
        currentTable = currentTable.parent;
    }

    public void insert(String id, T s) throws VariableAlreadyDeclaredException {
        if (currentTable.entries.containsKey(id)) {
            throw new VariableAlreadyDeclaredException(id);
        }
        currentTable.entries.put(id, s);
    }

    public T lookup(String id) throws VariableNotDeclaredException {
        return currentTable.recursiveLookup(id);
    }

    private T recursiveLookup(String id) throws VariableNotDeclaredException {
        if (entries.containsKey(id)) {
            return entries.get(id);
        }
        if (parent == null) {
            throw new VariableNotDeclaredException(id);
        }
        return parent.recursiveLookup(id);
    }

    public RecursiveSymbolTable<T> clone() {
        return new RecursiveSymbolTable<>(this.parent);
    }

    public void change(String name, T element) {
        currentTable.entries.put(name, element);
    }
}
