package com.d401f17.TypeSystem;

import com.d401f17.SymbolTable.MemberNotFoundException;

import java.util.HashMap;

public class RecordType extends Type {
    private String name;
    private HashMap<String, Type> members = new HashMap<>();

    public String getName() {
        return name;
    }

    public RecordType() {
        this("test", null, null);
    }

    public RecordType(String name) {
        this(name, null, null);
    }

    public HashMap<String, Type> getMembers() {
        return members;
    }

    public RecordType(String name, String[] memberNames, Type[] memberTypes) {
        this.name = name;
        setMembers(memberNames, memberTypes);
    }

    public void setMembers(String[] memberNames, Type[] memberTypes) {
        if (memberNames != null && memberNames.length == memberTypes.length) {
            for (int i = 0; i < memberNames.length; i++) {
                members.put(memberNames[i], memberTypes[i]);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RecordType that = (RecordType) o;

        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public String toString() {
        return "RECORD " + name.toUpperCase();
    }

    public Type getMemberType(String id) throws MemberNotFoundException {
        if (members.containsKey(id)) {
            return members.get(id);
        } else {
            throw new MemberNotFoundException(toString() + " does not contain member " + id);
        }
    }

    @Override
    public String getJavaType() {
        return null;
    }

    public static RecordType textfile = new RecordType("textfile", new String[]{"error", "directory", "name"}, new Type[]{new IntType(),new StringType(), new StringType()});
    public static RecordType binfile = new RecordType("binfile", new String[]{"error", "directory", "name"}, new Type[]{new IntType(),new StringType(), new StringType()});
}