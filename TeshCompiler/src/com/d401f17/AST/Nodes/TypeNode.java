package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.ArrayType;
import com.d401f17.AST.TypeSystem.RecordType;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/31/17.
 */
public class TypeNode extends AST {
    public TypeNode(String primitiveType) {
        if (primitiveType.substring(0, Math.min(primitiveType.length(), 6)).equals("record")) {
            this.setType(new RecordType(primitiveType.substring(6, primitiveType.length()), null, null));
        } else if (primitiveType.endsWith("[]")) {
            String[] parts = primitiveType.split("\\[");
            this.setType(new ArrayType(Types.ARRAY, new Type(Types.valueOf(parts[0].toUpperCase())), parts.length - 2));
        }
        else {
            try {
                Types prim = Types.valueOf(primitiveType.toUpperCase());
                this.setType(new Type(prim));
            } catch (IllegalArgumentException e) {
                this.setType(new Type(Types.ERROR, "Unknown type: " + primitiveType));
            }
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
