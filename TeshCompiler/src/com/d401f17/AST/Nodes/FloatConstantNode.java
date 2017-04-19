package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class FloatConstantNode extends ConstantNode {

    public FloatConstantNode(float value) {
        super(value, Types.FLOAT);
    }

    public FloatConstantNode(float value, int lineNumber) {
        super(value, Types.FLOAT, lineNumber);
    }

    public Float getValue() {
        return (Float)super.getValue();
    }

    public void setValue(Float value) {
        super.setValue(value);
    }
}
