package com.d401f17.AST.TypeSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrik on 17-04-2017.
 */
public class RecordType extends Type {
    private String name;
    private List<Type> args = new ArrayList<>();

    public RecordType(String name, Type ... formalArguments) {
        super(Types.RECORD);

        this.name = name;
        for (Type t : formalArguments) {
            this.args.add(t);
        }
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

}