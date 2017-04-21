package com.d401f17.AST.TypeSystem;

/**
 * Created by hense on 4/5/17.
 */
public class Type implements Comparable<Type> {
    private Types primitiveType;
    private int lineNum;
    private String errorMessage;

    public Type(Types primitiveType) {
        this.primitiveType = primitiveType;
    }

    public Type(Types primitiveType, int lineNum, String errorMessage) {
        this.primitiveType = primitiveType;
        this.lineNum = lineNum;
        this.errorMessage = errorMessage;
    }

    public Types getPrimitiveType() {
        return primitiveType;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getErrorMessage() {
        return "Error on line " + lineNum + ": " + errorMessage;
    }

    public boolean isSubtypeOf(Types t) {
        switch (t) {
            case INT:
                return primitiveType == Types.INT;
            case FLOAT:
                return primitiveType == Types.INT || primitiveType == Types.FLOAT;
            case CHAR:
                return primitiveType == Types.INT || primitiveType == Types.CHAR;
            case STRING:
                return primitiveType == Types.INT || primitiveType == Types.FLOAT || primitiveType == Types.STRING || primitiveType == Types.CHAR || primitiveType == Types.BOOL;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return primitiveType.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Type && primitiveType == ((Type) o).getPrimitiveType();
    }

    @Override
    public int compareTo(Type o) {
        return lineNum - o.lineNum;
    }
}
