package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.Type;
import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/20/17.
 */
public class RecordLiteralNode extends LiteralNode {
    public RecordLiteralNode(List<LiteralNode> value, Type primitiveType) {
        super(value, primitiveType);
    }

    public RecordLiteralNode(List<LiteralNode> value, Type primitiveType, int lineNum) {
        super(value, primitiveType, lineNum);
    }

    @Override
    public List<LiteralNode> getValue() {
        return (List<LiteralNode>)super.getValue();
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
