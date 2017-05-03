package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;
import com.d401f17.TypeSystem.FloatType;

/**
 * Created by mathias on 4/19/17.
 */
public class FloatLiteralNode extends LiteralNode {

    public FloatLiteralNode(double value) {
        super(value, new FloatType());
    }

    public FloatLiteralNode(double value, int lineNumber) {
        super(value, new FloatType(), lineNumber);
    }

    public Double getValue() {
        return (Double)super.getValue();
    }

    public void setValue(Double value) {
        super.setValue(value);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
