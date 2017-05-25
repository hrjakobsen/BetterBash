package com.d401f17.TypeSystem;

public class ArrayType extends Type {
    private Type childType;

    public Type getChildType() {
        return childType;
    }

    public ArrayType() {
        this(new IntType());
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

        return childType != null ? (childType.equals(arrayType.childType) || childType instanceof VoidType || arrayType.childType instanceof VoidType) : arrayType.childType == null;
    }

    @Override
    public String getJavaType() {
        return "Ljava/util/ArrayList<" + getChildType().getJavaType() + ">;";
    }

    public Type getInnerMostType() {
        return childType instanceof ArrayType ? ((ArrayType)childType).getInnerMostType() : childType;
    }
}
