package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.Type;
import com.d401f17.Visitors.ASTVisitor;

import java.util.HashMap;
import java.util.List;

public class RecordLiteralNode extends LiteralNode {
    public RecordLiteralNode(HashMap<String, LiteralNode> value, Type primitiveType) {
        super(value, primitiveType);
    }

    public RecordLiteralNode(HashMap<String, LiteralNode> value, Type primitiveType, int lineNum) {
        super(value, primitiveType, lineNum);
    }

    @Override
    public HashMap<String, LiteralNode> getValue() {
        return (HashMap<String, LiteralNode>)super.getValue();
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
