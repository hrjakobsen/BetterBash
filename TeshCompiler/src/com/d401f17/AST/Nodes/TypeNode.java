package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.RecordType;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class TypeNode extends AST {
    public TypeNode(String primitiveType) {
        if (primitiveType.equals("int")) {
            this.setType(new Type(Types.INT));
        } else if (primitiveType.equals("float")) {
            this.setType(new Type(Types.FLOAT));
        } else if (primitiveType.equals("char")) {
            this.setType(new Type(Types.CHAR));
        } else if (primitiveType.equals("string")) {
            this.setType(new Type(Types.STRING));
        } else if (primitiveType.equals("bool")) {
            this.setType(new Type(Types.BOOL));
        } else if (primitiveType.equals("array")) {
            this.setType(new Type(Types.ARRAY));
        } else if (primitiveType.equals("channel")) {
            this.setType(new Type(Types.CHANNEL));
        } else if (primitiveType.substring(0, Math.min(primitiveType.length(), 6)).equals("record")) {
            this.setType(new RecordType(primitiveType.substring(6, primitiveType.length())));
        } else if (primitiveType.equals("file")) {
            this.setType(new Type(Types.FILE));
        } else if (primitiveType.equals("void")) {
            this.setType(new Type(Types.VOID));
        } else {
            this.setType(new Type(Types.ERROR, "Unknown type: " + primitiveType));
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
