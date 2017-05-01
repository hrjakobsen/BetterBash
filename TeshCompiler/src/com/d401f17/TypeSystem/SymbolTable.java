package com.d401f17.TypeSystem;

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

    public SymbolTable(SymbolTable old) {
        tables = (ArrayList<HashMap<String, Symbol>>)old.tables.clone();
        scopeLevel = old.scopeLevel;
    }

    public void openScope() {
        tables.add(new HashMap<>());
        scopeLevel++;
    }

    public void closeScope() {
        tables.remove(scopeLevel);
        scopeLevel--;
    }

    public void insert(String id, Symbol s) throws VariableAlreadyDeclaredException {
        if (tables.get(scopeLevel).containsKey(id)) {
            throw new VariableAlreadyDeclaredException(id + " already declared in the current scope");
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
        throw new VariableNotDeclaredException(id + " not declared");
    }

    public static SymbolTable StandardTable() {
        SymbolTable table = new SymbolTable();
        try {
            table.insert("str(INT)", new FunctionSymbol(new FunctionType("str(INT)", new Type[] {new IntType()}, new StringType()), null, null));
            table.insert("str(FLOAT)", new FunctionSymbol(new FunctionType("str(FLOAT)", new Type[] {new FloatType()}, new StringType()), null, null));
            table.insert("str(CHAR)", new FunctionSymbol(new FunctionType("str(CHAR)", new Type[] {new CharType()}, new StringType()), null, null));
            table.insert("str(BOOL)", new FunctionSymbol(new FunctionType("str(BOOL)", new Type[] {new BoolType()}, new StringType()), null, null));
            table.insert("str(STRING)", new FunctionSymbol(new FunctionType("str(STRING)", new Type[] {new StringType()}, new StringType()), null, null));
            table.insert("stdio", new Symbol(new ChannelType(), null));
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return table;
    }
}