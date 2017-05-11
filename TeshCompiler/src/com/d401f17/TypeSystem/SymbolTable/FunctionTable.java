package com.d401f17.TypeSystem.SymbolTable;

import com.d401f17.TypeSystem.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hense on 5/11/17.
 */
public class FunctionTable extends SymbolTable {
    private ArrayList<HashMap<String, ArrayList<Symbol>>> tables = new ArrayList<>();
    private int scopeLevel = 0;

    public FunctionTable() {
        tables.add(new HashMap<>());
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
        ArrayList<Symbol> functionList;
        if (tables.get(scopeLevel).containsKey(id)) {
            functionList = tables.get(scopeLevel).get(id);
            for (Symbol functionSymbol : functionList) {
                if (((FunctionType)functionSymbol.getType()).getArgs().equals(((FunctionType)s.getType()).getArgs())) {
                    throw new VariableAlreadyDeclaredException(id + " already declared in the current scope");
                }
            }
        } else {
            functionList = new ArrayList<>();
        }
        functionList.add(s);
        tables.get(scopeLevel).put(id, functionList);
    }

    public Symbol lookup(String id, List<Type> argTypes) throws VariableNotDeclaredException {
        int i = scopeLevel;
        while (i >= 0) {
            if (tables.get(i).containsKey(id)) {
                Symbol bestMatch = getBestMatch(tables.get(scopeLevel).get(id), argTypes);
                if (bestMatch != null) {
                    return bestMatch;
                }
            }
            i--;
        }
        throw new VariableNotDeclaredException(id + " not declared");
    }

    private Symbol getBestMatch(List<Symbol> functionList, List<Type> argTypes) {
        for (Symbol functionSymbol : functionList) {
            boolean validSignature = true;
            List<Type> functionArgs = ((FunctionType)functionSymbol.getType()).getArgs();
            for (int i = 0; i < functionArgs.size(); i++) {
                if (!functionArgs.get(i).getClass().isInstance(argTypes.get(i))) {
                    validSignature = false;
                    break;
                }
            }
            if (validSignature) {
                return functionSymbol;
            }
        }

        return null;
    }

    public static FunctionTable StandardTable() {
        FunctionTable table = new FunctionTable();
        try {
            table.insert("str", new FunctionSymbol(new FunctionType("str", new ArrayList<Type>() {{ add(new IntType()); }}, new StringType()), null, null));
            table.insert("str", new FunctionSymbol(new FunctionType("str", new ArrayList<Type>() {{ add(new FloatType()); }}, new StringType()), null, null));
            table.insert("str", new FunctionSymbol(new FunctionType("str", new ArrayList<Type>() {{ add(new CharType()); }}, new StringType()), null, null));
            table.insert("str", new FunctionSymbol(new FunctionType("str", new ArrayList<Type>() {{ add(new BoolType()); }}, new StringType()), null, null));
            table.insert("str", new FunctionSymbol(new FunctionType("str", new ArrayList<Type>() {{ add(new StringType()); }}, new StringType()), null, null));
            table.insert("print", new FunctionSymbol(new FunctionType("print", new ArrayList<Type>() {{ add(new StringType()); }}, new VoidType()), null, null));
            table.insert("read", new FunctionSymbol(new FunctionType("read", new ArrayList<Type>(), new StringType()), null, null));
        } catch (VariableAlreadyDeclaredException e) {}
        return table;
    }
}
