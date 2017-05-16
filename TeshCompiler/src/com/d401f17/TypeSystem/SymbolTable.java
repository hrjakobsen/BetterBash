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
            table.insert("str(INT)", new FunctionSymbol(new FunctionType("str", new Type[] {new IntType()}, new StringType()), null, null));
            table.insert("str(FLOAT)", new FunctionSymbol(new FunctionType("str", new Type[] {new FloatType()}, new StringType()), null, null));
            table.insert("str(CHAR)", new FunctionSymbol(new FunctionType("str", new Type[] {new CharType()}, new StringType()), null, null));
            table.insert("str(BOOL)", new FunctionSymbol(new FunctionType("str", new Type[] {new BoolType()}, new StringType()), null, null));
            table.insert("str(STRING)", new FunctionSymbol(new FunctionType("str", new Type[] {new StringType()}, new StringType()), null, null));
            table.insert("print(STRING)", new FunctionSymbol(new FunctionType("print", new Type[] {new StringType()}, new VoidType()), null, null));
            table.insert("read()", new FunctionSymbol(new FunctionType("read", new Type[] {}, new StringType()), null, null));
            table.insert("sqrt(INT)", new FunctionSymbol(new FunctionType("sqrt", new Type[] {new IntType()}, new FloatType()), null, null));
            table.insert("sqrt(FLOAT)", new FunctionSymbol(new FunctionType("sqrt", new Type[] {new FloatType()}, new FloatType()), null, null));
            table.insert("rnd()", new FunctionSymbol(new FunctionType("rnd", new Type[] {}, new FloatType()), null, null));
            table.insert("ceil(FLOAT)", new FunctionSymbol(new FunctionType("ceil", new Type[] {new FloatType()}, new IntType()), null, null));
            table.insert("floor(FLOAT)", new FunctionSymbol(new FunctionType("float", new Type[] {new FloatType()}, new IntType()), null, null));
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return table;
    }
}