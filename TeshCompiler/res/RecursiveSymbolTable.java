import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;


public class RecursiveSymbolTable {
    private RecursiveSymbolTable parent = null;
    private RecursiveSymbolTable currentTable = this;
    private HashMap<String, Object> entries = new HashMap<>();

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
    
    public void openScope() {
        currentTable = new RecursiveSymbolTable(currentTable);
    }
    
    public void closeScope() {
        currentTable = currentTable.parent;
    }
    
    public void insert(String id, Object s) {
        currentTable.entries.put(id, s);
    }
    
    public Object lookup(String id) {
        Object a =  currentTable.recursiveLookup(id);

        return a;
    }

    private Object recursiveLookup(String id) {
        if (entries.containsKey(id)) {
            return entries.get(id);
        }
        return parent.recursiveLookup(id);
    }

    public void change(String name, Object s) {
        if (!currentTable.recursiveChange(name, s)) {
            currentTable.entries.put(name, s);
        }
    }

    private boolean recursiveChange(String name, Object s) {
        if (entries.containsKey(name)) {
          entries.put(name, s);
          return true;
        } else {
            if (parent == null) return false;
            return parent.recursiveChange(name, s);
        }
    }

    public RecursiveSymbolTable clone() {
        RecursiveSymbolTable rec = new RecursiveSymbolTable();
        rec.parent = currentTable.parent;
        rec.entries = (HashMap<String, Object>)currentTable.entries.clone();
        return rec;
    }
}

