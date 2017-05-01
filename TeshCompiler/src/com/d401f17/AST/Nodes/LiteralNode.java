package com.d401f17.AST.Nodes;

import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.ASTVisitor;

public class LiteralNode extends ArithmeticExpressionNode {
    private Object value;

    public LiteralNode(Object value, Type primitiveType, int lineNum) {
        this.value = value;
        this.setType(primitiveType);
        this.lineNum = lineNum;
    }

    public LiteralNode(Object value, Type primitiveType) {
        this.value = value;
        this.setType(primitiveType);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }

    public static LiteralNode fromType(Type t) {
        if (t instanceof IntType){
           return new IntLiteralNode(0);
        } else if (t instanceof FloatType) {
            return new FloatLiteralNode(0);
        } else if (t instanceof StringType) {
            return new StringLiteralNode("");
        } else if (t instanceof CharType) {
            return new CharLiteralNode('0');
        } else if (t instanceof BoolType) {
            return new BoolLiteralNode(false);
        }
        else {
            return null;
        }
    }
}
