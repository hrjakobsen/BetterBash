package com.d401f17.AST.TypeSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hense on 4/7/17.
 */
public class FunctionType extends Type {
    private List<Type> args = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public FunctionType(Types primitiveType, Type[] formalArguments) {
        super(primitiveType);

        for (Type t : formalArguments) {
            this.args.add(t);
        }
    }
}
