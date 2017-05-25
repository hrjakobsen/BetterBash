package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.BoolType;
import com.d401f17.Visitors.ASTVisitor;

public class BoolLiteralNode extends LiteralNode {
    public BoolLiteralNode(boolean value) {
        super(value, new BoolType());
    }

    public BoolLiteralNode(boolean value, int lineNumber) {
        super(value, new BoolType(), lineNumber);
    }

    public Boolean getValue() {
        return (Boolean) super.getValue();
    }

    public void setValue(Boolean value) {
        super.setValue(value);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
