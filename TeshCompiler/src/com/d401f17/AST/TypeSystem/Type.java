package com.d401f17.AST.TypeSystem;

/**
 * Created by hense on 4/5/17.
 */
public class Type {
    private Types primitiveType;
    private String errorMessage;

    public Type(Types primitiveType) {
        this(primitiveType, null);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Type(Types primitiveType, String errorMessage) {
        this.primitiveType = primitiveType;
        this.errorMessage = errorMessage;
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
