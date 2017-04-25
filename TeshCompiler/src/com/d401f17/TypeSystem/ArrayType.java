package com.d401f17.TypeSystem;

/**
 * Created by mathias on 4/19/17.
 */
public class ArrayType extends Type {
    private Type childType;

    public Type getChildType() {
        return childType;
    }

    public Type getInnermostChildType() {
        Type inner = childType;
        while (inner instanceof ArrayType) {
            inner = ((ArrayType) inner).getChildType();
        }
        return inner;
    }

    public ArrayType() {
    }

    public ArrayType(Type childType) {
        this.childType = childType;
    }

    public ArrayType(Type childType, int depth) {
        if (depth <= 0) {
            this.childType = childType;
        } else {
            this.childType = new ArrayType(childType, depth - 1);
        }
    }

    @Override
    public String toString() {
        return childType.toString() + "[]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ArrayType arrayType = (ArrayType) o;

        return childType != null ? childType.equals(arrayType.childType) : arrayType.childType == null;
    }
}
