package com.d401f17.AST.TypeSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Henrik on 17-04-2017.
 */
public class RecordType extends Type {
    private String name;
    private HashMap<String, Type> members = new HashMap<>();

    public String getName() {
        return name;
    }

    public RecordType(String name, String[] memberNames, Type[] memberTypes) {
        super(Types.RECORD);
        this.name = name;

        if (memberNames != null && memberNames.length == memberTypes.length) {
            for (int i = 0; i < memberNames.length; i++) {
                members.put(memberNames[i], memberTypes[i]);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public Type getMemberType(String id) throws MemberNotFoundException {
        if (members.containsKey(id)) {
            return members.get(id);
        } else {
            throw new MemberNotFoundException("Record " + name + " does not contain member " + id);
        }
    }
}