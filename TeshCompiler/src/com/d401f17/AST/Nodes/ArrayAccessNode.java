package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.List;

/**
 * Created by mathias on 4/4/17.
 */
public class ArrayAccessNode extends IdentifierNode {
    public IdentifierNode array;
    public List<ArithmeticExpressionNode> indices;

    public IdentifierNode getArray() {
        return array;
    }

    public void setArray(IdentifierNode array) {
        this.array = array;
    }

    public List<ArithmeticExpressionNode> getIndices() {
        return indices;
    }

    public void setIndices(List<ArithmeticExpressionNode> indices) {
        this.indices = indices;
    }

    public ArrayAccessNode(IdentifierNode array, List<ArithmeticExpressionNode> indices) {
        this.array = array;

        this.indices = indices;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}