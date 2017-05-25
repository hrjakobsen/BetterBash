package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.CharType;
import com.d401f17.Visitors.ASTVisitor;

public class CharLiteralNode extends LiteralNode {
    public CharLiteralNode(Character value) {
        super(value, new CharType());
    }
    public CharLiteralNode(Character value, int lineNumber) {
        super(value, new CharType(), lineNumber);
    }

    public Character getValue() {
        return (Character) super.getValue();
    }

    public void setValue(Character value) {
        super.setValue(value);
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
