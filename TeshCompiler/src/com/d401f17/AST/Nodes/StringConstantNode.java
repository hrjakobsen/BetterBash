package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class StringConstantNode extends ConstantNode {
    public StringConstantNode(String value) {
        super(value, Types.STRING);
    }
    public StringConstantNode(String value, int lineNumber) {
        super(value, Types.STRING, lineNumber);
    }

    public String getValue() {
        return (String)super.getValue();
    }

    public void setValue(String value) {
        super.setValue(value);
    }
}
