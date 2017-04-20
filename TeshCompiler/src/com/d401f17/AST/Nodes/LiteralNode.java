package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.ASTVisitor;

public class LiteralNode extends ArithmeticExpressionNode {
    private Object value;

    public LiteralNode(Object value, Types primitiveType, int lineNum) {
        this.value = value;
        this.setType(new Type(primitiveType));
        this.lineNum = lineNum;
    }

    public LiteralNode(Object value, Types primitiveType) {
        this.value = value;
        this.setType(new Type(primitiveType));
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
