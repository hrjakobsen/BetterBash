package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class BoolConstantNode extends ConstantNode {
    public BoolConstantNode(boolean value) {
        super(value, Types.BOOL);
    }

    public BoolConstantNode(boolean value, int lineNumber) {
        super(value, Types.BOOL, lineNumber);
    }

    public Boolean getValue() {
        return (Boolean) super.getValue();
    }

    public void setValue(Boolean value) {
        super.setValue(value);
    }
}
