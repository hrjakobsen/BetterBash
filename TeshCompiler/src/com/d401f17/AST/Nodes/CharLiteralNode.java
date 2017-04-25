package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.CharType;
import com.d401f17.TypeSystem.Types;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/20/17.
 */
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
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
