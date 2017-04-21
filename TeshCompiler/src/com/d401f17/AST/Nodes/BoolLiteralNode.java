package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/19/17.
 */
public class BoolLiteralNode extends LiteralNode {
    public BoolLiteralNode(boolean value) {
        super(value, Types.BOOL);
    }

    public BoolLiteralNode(boolean value, int lineNumber) {
        super(value, Types.BOOL, lineNumber);
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
