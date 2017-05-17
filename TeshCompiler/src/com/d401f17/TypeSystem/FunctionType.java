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

    public FunctionType(String name, Type[] formalArguments, Type returnType) {
        this.returnType = returnType;
        for (Type t : formalArguments) {
            this.args.add(t);
        }

        createSignature(name, formalArguments);
    }

    public boolean isValidCallOf(FunctionType other) {
        if (args.size() != other.args.size()) {
            return false;
        }

        boolean valid = true;
        for (int i = 0; i < args.size(); i++) {
            Type arg = args.get(i);
            Type argOther = other.args.get(i);

            if (!argOther.getClass().isInstance(arg)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FunctionType that = (FunctionType) o;

        return args != null ? args.equals(that.args) : that.args == null;
    }

    @Override
    public int hashCode() {
        return args != null ? args.hashCode() : 0;
    }

    @Override
    public String toString() {
        return signature;
    }

    private void createSignature(String name, Type[] formalArguments) {
        int numArgs = formalArguments.length;
        StringBuilder sb = new StringBuilder();
        sb.append(name + "(");
        if (numArgs > 0) {
            for (int i = 0; i < numArgs - 1; i++) {
                sb.append(formalArguments[i] + ", ");
            }
            sb.append(formalArguments[numArgs - 1]);
        }
        sb.append(")");
        signature = sb.toString();
    }
}
