package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.IntType;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/19/17.
 */
public class IntLiteralNode extends LiteralNode {

    public IntLiteralNode(long value) {
        super(value, new IntType());
    }

    public IntLiteralNode(long value, int lineNumber) {
        super(value, new IntType(), lineNumber);
    }

    public Long getValue() {
        return (Long)super.getValue();
    }

    public void setValue(Long value) {
        super.setValue(value);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
