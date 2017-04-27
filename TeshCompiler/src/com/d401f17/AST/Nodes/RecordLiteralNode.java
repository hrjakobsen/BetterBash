package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.Type;

/**
 * Created by mathias on 4/20/17.
 */
public class RecordLiteralNode extends LiteralNode {
    public RecordLiteralNode(Object value, Type primitiveType) {
        super(value, primitiveType);
    }

    public RecordLiteralNode(Object value, Type primitiveType, int lineNum) {
        super(value, primitiveType, lineNum);
    }
}
