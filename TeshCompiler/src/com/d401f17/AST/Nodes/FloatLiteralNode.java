package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.FloatType;
import com.d401f17.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class FloatLiteralNode extends LiteralNode {

    public FloatLiteralNode(float value) {
        super(value, new FloatType());
    }

    public FloatLiteralNode(float value, int lineNumber) {
        super(value, new FloatType(), lineNumber);
    }

    public Float getValue() {
        return (Float)super.getValue();
    }

    public void setValue(Float value) {
        super.setValue(value);
    }
}
