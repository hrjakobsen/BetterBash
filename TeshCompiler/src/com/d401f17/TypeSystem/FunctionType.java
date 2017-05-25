package com.d401f17.TypeSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunctionType extends Type {
    private String signature;
    private List<Type> args = new ArrayList<>();
    private Type returnType;

    public Type getReturnType() {
        return returnType;
    }

    public FunctionType(String name, Type[] formalArguments, Type returnType) {
        this.returnType = returnType;
        if (formalArguments != null) {
            this.args.addAll(Arrays.asList(formalArguments));
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

            if (arg instanceof ArrayType && argOther instanceof ArrayType) {
                if (!matchingArrayTypes((ArrayType)arg, (ArrayType)argOther)) {
                    valid = false;
                    break;
                }
            }

            if (!argOther.getClass().isInstance(arg)) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    private boolean matchingArrayTypes(ArrayType a1, ArrayType a2) {
        if (a1.getChildType().getClass() == a2.getChildType().getClass()) {
            if (a1.getChildType() instanceof ArrayType) {
                return matchingArrayTypes((ArrayType)a1.getChildType(), (ArrayType)a2.getChildType());
            }
            return true;
        }
        return false;
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
        int numArgs = formalArguments == null ? 0 : formalArguments.length;
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

    public List<Type> getArgs() {
        return args;
    }

    public void setArgs(List<Type> args) {
        this.args = args;
    }

    @Override
    public String getJavaType() {
        return null;
    }
}
