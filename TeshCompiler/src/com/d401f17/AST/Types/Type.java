package com.d401f17.AST.Types;

/**
 * Created by hense on 4/5/17.
 */
public class Type {
    private Types primitiveType;

    public Type(Types primitiveType) {

        this.primitiveType = primitiveType;
    }

    public Types getPrimitiveType() {
        return primitiveType;
    }

    public void setPrimitiveType(Types primitiveType) {
        this.primitiveType = primitiveType;
    }

    @Override
    public String toString() {
        return primitiveType.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Type && primitiveType == ((Type) o).getPrimitiveType();
    }
}
