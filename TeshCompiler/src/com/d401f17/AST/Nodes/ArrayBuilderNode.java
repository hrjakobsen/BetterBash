package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

/**
 * Created by mathias on 4/4/17.
 */
public class ArrayBuilderNode extends StatementNode {
    public SimpleIdentifierNode variable;
    public ArithmeticExpressionNode expression;
    public IdentifierNode array;

    public ArrayBuilderNode(SimpleIdentifierNode variable, IdentifierNode array, ArithmeticExpressionNode expression, int lineNum) {
        this.variable = variable;
        this.expression = expression;
        this.array = array;
        this.setLine(lineNum);
    }

    public SimpleIdentifierNode getVariable() {
        return variable;
    }

    public void setVariable(SimpleIdentifierNode variable) {
        this.variable = variable;
    }

    public ArithmeticExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(ArithmeticExpressionNode expression) {
        this.expression = expression;
    }

    public IdentifierNode getArray() {
        return array;
    }

    public void setArray(IdentifierNode array) {
        this.array = array;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
