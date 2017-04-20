package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;

/**
 * Created by mathias on 4/19/17.
 */
public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String value) {
        super(value, Types.STRING);
    }
    public StringLiteralNode(String value, int lineNumber) {
        super(value, Types.STRING, lineNumber);
    }

    public String getValue() {
        return (String)super.getValue();
    }

    public void setValue(String value) {
        super.setValue(value);
    }
}
