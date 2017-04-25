package com.d401f17.AST.Nodes;

import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/19/17.
 */
public class ArrayLiteralNode extends LiteralNode {
    public ArrayLiteralNode(List<ArithmeticExpressionNode> value, int lineNum) {
        super(value, Types.ARRAY, lineNum);
    }

    public ArrayLiteralNode(List<ArithmeticExpressionNode> value) {
        super(value, Types.ARRAY);
    }

    public void setValue(List<ArithmeticExpressionNode> value) {
        super.setValue(value);
    }

    public List<ArithmeticExpressionNode> getValue() {
        return (List<ArithmeticExpressionNode>)super.getValue();
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}