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

    public boolean isSubTypeOf(Type type) {
        return isSubtypeOf(type.getPrimitiveType());
    }

    public boolean isSubtypeOf(Types t) {
        if (t == primitiveType) {
            return true;
        }

        switch (t) {
            case FLOAT:
                return primitiveType == Types.INT;
            case CHAR:
                return primitiveType == Types.INT;
            case STRING:
                return primitiveType == Types.INT || primitiveType == Types.FLOAT || primitiveType == Types.CHAR || primitiveType == Types.BOOL;
            default:
                return false;
        }
    }

    public Types implicitConversion(Type type) throws InvalidConversionException {
        return implicitConversion(type.getPrimitiveType());
    }

    public Types implicitConversion(Types t) throws InvalidConversionException {
        switch (t) {
            case INT:
                switch (primitiveType) {
                    case INT:
                        return Types.INT;
                    case FLOAT:
                        return Types.FLOAT;
                    case CHAR:
                        return Types.CHAR;
                    case STRING:
                        return Types.STRING;
                    default:
                        throw new InvalidConversionException("Could not convert " + primitiveType + " to " + t);
                }
            case FLOAT:
                switch (primitiveType) {
                    case INT:
                    case FLOAT:
                        return Types.FLOAT;
                    case STRING:
                        return Types.STRING;
                    default:
                        throw new InvalidConversionException("Could not convert " + primitiveType + " to " + t);
                }
            case CHAR:
                switch (primitiveType) {
                    case INT:
                    case CHAR:
                        return Types.CHAR;
                    case STRING:
                        return Types.STRING;
                    default:
                        throw new InvalidConversionException("Could not convert " + primitiveType + " to " + t);
                }
            case STRING:
                switch (primitiveType) {
                    case INT:
                    case FLOAT:
                    case CHAR:
                    case STRING:
                        return Types.STRING;
                    default:
                        throw new InvalidConversionException("Could not convert " + primitiveType + " to " + t);
                }
            default:
                throw new InvalidConversionException("Could not convert " + primitiveType + " to " + t);
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
