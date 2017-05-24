import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;
/**
 * Created by mathias on 4/28/17.
 */
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
        currentTable = new RecursiveSymbolTable(this);
    }
    
    public void closeScope() {
        currentTable = currentTable.parent;
    }
    
    public void insert(String id, Object s) {
        //System.out.println("inserting: " + id);

        //System.out.print("I " + s + " → " + Arrays.asList(entries) + " → ");
        currentTable.entries.put(id, s);
        //System.out.print(Arrays.asList(entries) + "(" + this.toString() + ")" + "\n");
        //System.out.println("Entries are now: " + Arrays.asList(entries).toString());
    }
    
    public Object lookup(String id) {
        //System.out.println("looking for: " + id);
        //System.out.print("L "+ "(" + currentTable + ")" + id  + " → ");
        Object a =  currentTable.recursiveLookup(id);
        //System.out.print(a.toString() + " ) " + "\n");

        return a;
    }

    private Object recursiveLookup(String id) {
        //System.out.println("Looking in " + this);
        if (entries.containsKey(id)) {
            return entries.get(id);
        }
        return parent.recursiveLookup(id);
    }

    public void change(String name, Object s) {
        //System.out.println("E (" + name + " → " + s.toString() + ") (" + currentTable + ")" );
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

