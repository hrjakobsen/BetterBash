package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.StringType;
import com.d401f17.Visitors.ASTVisitor;

public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String value) {
        super(value, new StringType());
    }
    public StringLiteralNode(String value, int lineNumber) {
        super(value, new StringType(), lineNumber);
    }

    public String getValue() {
        return (String)super.getValue();
    }

    public void setValue(String value) {
        super.setValue(value);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return getValue();
    }
}
