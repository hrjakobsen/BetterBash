package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.ArrayType;
import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

public class ValueArrayLiteralNode extends LiteralNode {
    public ValueArrayLiteralNode(List<LiteralNode> value, int lineNum) {
        super(value, new ArrayType(), lineNum);
    }

    public ValueArrayLiteralNode(List<LiteralNode> value) {
        super(value, new ArrayType());
    }

    public void setValue(List<LiteralNode> value) {
        super.setValue(value);
    }

    public List<LiteralNode> getValue() {
        return (List<LiteralNode>)super.getValue();
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
