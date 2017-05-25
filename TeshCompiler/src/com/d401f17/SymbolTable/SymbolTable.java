package com.d401f17.SymbolTable;

import com.d401f17.TypeSystem.IntType;
import com.d401f17.TypeSystem.RecordType;
import com.d401f17.TypeSystem.StringType;
import com.d401f17.TypeSystem.Type;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Henrik on 05-04-2017.
 */
public class SymbolTable implements SymTab {
    private ArrayList<HashMap<String, Symbol>> tables = new ArrayList<>();
    private int scopeLevel = 0;

    public SymbolTable() {
        tables.add(new HashMap<>());
    }

    public SymbolTable(SymbolTable old) {
        tables = (ArrayList<HashMap<String, Symbol>>)old.tables.clone();
        scopeLevel = old.scopeLevel;
    }

    @Override
    public void openScope() {
        tables.add(new HashMap<>());
        scopeLevel++;
    }

    @Override
    public void closeScope() {
        tables.remove(scopeLevel);
        scopeLevel--;
    }

    @Override
    public void insert(String id, Symbol s) throws VariableAlreadyDeclaredException {
        if (tables.get(scopeLevel).containsKey(id)) {
            throw new VariableAlreadyDeclaredException(id + " already declared in the current scope");
        } else {
            tables.get(scopeLevel).put(id, s);
        }
    }

    @Override
    public Symbol lookup(String id) throws VariableNotDeclaredException {
        int i = scopeLevel;
        while (i >= 0) {
            if (tables.get(i).containsKey(id)) {
                return tables.get(i).get(id);
            }
            i--;
        }
        throw new VariableNotDeclaredException(id + " not declared");
    }

    public static SymbolTable TableWithDefaultRecords() {
        SymbolTable st = new SymbolTable();
        try {
            st.insert("binfile", new Symbol(new RecordType("binfile", new String[]{"name", "directory", "error"}, new Type[]{new StringType(), new StringType(), new IntType()}), null));
            st.insert("textfile", new Symbol(new RecordType("textfile", new String[]{"name", "directory", "error"}, new Type[]{new StringType(), new StringType(), new IntType()}), null));
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return st;
    }
}