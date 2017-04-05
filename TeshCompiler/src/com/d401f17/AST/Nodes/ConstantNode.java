package com.d401f17.AST.Nodes;

import com.d401f17.AST.Types.Types;
import com.d401f17.Visitors.ASTVisitor;

public class ConstantNode extends ArithmeticExpressionNode {
    private Object value;
    private Types type;

    public ConstantNode(Object value, Types type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
