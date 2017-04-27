package com.d401f17.Visitors.Interpreter;

import com.d401f17.AST.Nodes.*;

/**
 * Created by mathias on 4/27/17.
 */
public final class StandardLib {
    public static StringLiteralNode LiteralToString(LiteralNode node) {
        if (node instanceof FloatLiteralNode || node instanceof IntLiteralNode || node instanceof CharLiteralNode) {
            return new StringLiteralNode(node.getValue().toString());
        }
        return null;
    }

    public static IntLiteralNode FloatToInt(LiteralNode node) {
        if (node instanceof FloatLiteralNode) {
            return new IntLiteralNode(((FloatLiteralNode)node).getValue().intValue());
        }
        return null;
    }
}
