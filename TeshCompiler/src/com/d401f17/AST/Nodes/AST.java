package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public abstract class AST {
    private Type type;
    private int lineNum;

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

    public abstract void accept(ASTVisitor visitor);
}
