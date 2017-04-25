package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class TypeNode extends AST {
    public TypeNode(String primitiveType) {
        this(primitiveType, 0);
    }

    public TypeNode(String primitiveType, int lineNum) {
        this.lineNum = lineNum;
        if (primitiveType.endsWith("[]")) {
            String[] parts = primitiveType.split("\\[");
            try {
                this.setType(new ArrayType(typeFromString(parts[0]), parts.length - 2));
            } catch (UnknownTypeException e) {
                this.setType(new ErrorType(lineNum, "Unknown inner type: " + e.getMessage()));
            }
        } else if (primitiveType.substring(0, Math.min(primitiveType.length(), 6)).equals("record")) {
            this.setType(new RecordType(primitiveType.substring(7, primitiveType.length()), null, null));
        } else {
            try {
                this.setType(typeFromString(primitiveType));
            } catch (UnknownTypeException e) {
                this.setType(new ErrorType(lineNum, "Unknown type " + e.getMessage()));
            }
        }
    }

    private Type typeFromString(String str) throws UnknownTypeException {
        if (str.equals("int")) {
            return new IntType();
        } else if (str.equals("float")) {
            return new FloatType();
        } else if (str.equals("char")) {
            return new CharType();
        } else if (str.equals("string")) {
            return new StringType();
        } else if (str.equals("bool")) {
            return new BoolType();
        } else if (str.equals("binfile")) {
            return new BinFileType();
        } else if (str.equals("textfile")) {
            return new TextFileType();
        } else if (str.equals("channel")) {
            return new ChannelType();
        } else if (str.equals("void")) {
            return new VoidType();
        } else {
            throw new UnknownTypeException(str.toUpperCase());
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
