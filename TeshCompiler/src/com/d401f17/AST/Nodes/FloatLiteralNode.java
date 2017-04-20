package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class FloatLiteralNode extends LiteralNode {

    public FloatLiteralNode(float value) {
        super(value, Types.FLOAT);
    }

    public FloatLiteralNode(float value, int lineNumber) {
        super(value, Types.FLOAT, lineNumber);
    }

    public Float getValue() {
        return (Float)super.getValue();
    }

    public void setValue(Float value) {
        super.setValue(value);
    }
}
