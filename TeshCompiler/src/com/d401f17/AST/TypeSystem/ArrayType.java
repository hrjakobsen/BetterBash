package com.d401f17.AST.TypeSystem;

/**
 * Created by mathias on 4/19/17.
 */
public class ArrayType extends Type {

    private Type childType;

    public Type getChildType() {
        return childType;
    }

    public void setChildType(Type childType) {
        this.childType = childType;
    }

    public ArrayType(Types primitiveType) {
        super(primitiveType);
    }

    public ArrayType(Types primitiveType, Type childType) {
        super(primitiveType);
        this.childType = childType;
    }

    public ArrayType(Types primitiveType, Type childType, int depth) {
        super(primitiveType);
        if (depth <= 0) {
            this.childType = childType;
        } else {
            this.childType = new ArrayType(Types.ARRAY, childType, depth - 1);
        }
    }

    @Override
    public String toString() {
        return childType.toString().toUpperCase() + "[]";
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
