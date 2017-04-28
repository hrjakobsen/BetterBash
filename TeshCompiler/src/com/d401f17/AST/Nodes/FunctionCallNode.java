package com.d401f17.AST.Nodes;

import com.d401f17.Visitors.ASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mathias on 3/31/17.
 */
public class FunctionCallNode extends ArithmeticExpressionNode {
    private IdentifierNode name;
    private List<ArithmeticExpressionNode> arguments;

    public IdentifierNode getName() {
        return name;
    }

    public void setName(IdentifierNode name) {
        this.name = name;
    }

    public List<ArithmeticExpressionNode> getArguments() {
        return arguments;
    }

    public void setArguments(List<ArithmeticExpressionNode> arguments) {
        this.arguments = arguments;
    }

    public FunctionCallNode(IdentifierNode name, int lineNum, ArithmeticExpressionNode ... arguments) {
        this.name = name;
        this.lineNum = lineNum;
        this.arguments = new ArrayList<>(Arrays.asList(arguments));
    }

    public FunctionCallNode(IdentifierNode name, ArithmeticExpressionNode ... arguments) {
        this.name = name;
        this.arguments = new ArrayList<>(Arrays.asList(arguments));
    }

    @Override
    public Object accept(ASTVisitor visitor) {
        return visitor.visit(this);
    }
}
