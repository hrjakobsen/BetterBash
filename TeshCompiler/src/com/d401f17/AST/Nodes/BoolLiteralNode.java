package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.BoolType;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/19/17.
 */
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
