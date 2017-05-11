package com.d401f17.TypeSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hense on 4/7/17.
 */
public class FunctionType extends Type {
    private String signature;
    private List<Type> args = new ArrayList<>();
    private Type returnType;

    public Type getReturnType() {
        return returnType;
    }

    public FunctionType(String name, List<Type> formalArguments, Type returnType) {
        this.returnType = returnType;
        for (Type t : formalArguments) {
            this.args.add(t);
        }

        createSignature(name, formalArguments);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return signature;
    }

    public List<Type> getArgs() {
        return args;
    }

    private void createSignature(String name, List<Type> formalArguments) {
        int numArgs = formalArguments.size();
        StringBuilder sb = new StringBuilder();
        sb.append(name + "(");
        if (numArgs > 0) {
            for (int i = 0; i < numArgs - 1; i++) {
                sb.append(formalArguments.get(i) + ", ");
            }
            sb.append(formalArguments.get(numArgs - 1));
        }
        sb.append(")");
        signature = sb.toString();
    }
}
