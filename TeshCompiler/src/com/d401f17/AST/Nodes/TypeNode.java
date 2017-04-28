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
        primitiveType = primitiveType.toLowerCase();
        this.lineNum = lineNum;
        if (primitiveType.endsWith("[]")) {
            String[] parts = primitiveType.split("\\[");
            try {
                this.setType(new ArrayType(typeFromString(parts[0]), parts.length - 2));
            } catch (UnknownTypeException e) {
                this.setType(new ErrorType(lineNum, "Unknown inner type: " + e.getMessage()));
            }
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
        } else if (str.substring(0, Math.min(str.length(), 6)).equals("record")) {
            return new RecordType(str.substring(6, str.length()));
        } else {
            throw new UnknownTypeException(str.toUpperCase());
        }
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

}
