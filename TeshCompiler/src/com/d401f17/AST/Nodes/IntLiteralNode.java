package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class IntLiteralNode extends LiteralNode {

    public IntLiteralNode(int value) {
        super(value, Types.INT);
    }

    public IntLiteralNode(int value, int lineNumber) {
        super(value, Types.INT, lineNumber);
    }

    public Integer getValue() {
        return (Integer)super.getValue();
    }

    public void setValue(Integer value) {
        super.setValue(value);
    }
}
