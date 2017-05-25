package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.Type;
import com.d401f17.Visitors.ASTVisitor;

public abstract class AST {
    private Type type;
    protected int lineNum;

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


    public abstract Object accept(ASTVisitor visitor);
}
