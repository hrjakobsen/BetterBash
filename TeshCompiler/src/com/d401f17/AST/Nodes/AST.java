package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.Type;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public abstract class AST {
    private Type type;
    protected int lineNum;
    private LiteralNode nodeValue;

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        this.type = t;
    }

    public int getLine() {
        return lineNum;
    }
    public void setLine(int lineNum) {
        this.lineNum = lineNum;
    }

    public LiteralNode getNodeValue() {
        return nodeValue;
    }
    public void setNodeValue(LiteralNode value) {
        this.nodeValue = value;
    }

    public abstract void accept(ASTVisitor visitor);
}
