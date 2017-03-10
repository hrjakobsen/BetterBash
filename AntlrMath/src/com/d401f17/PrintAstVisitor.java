package com.d401f17;

/**
 * Created by mathias on 3/9/17.
 */
public class PrintAstVisitor extends AstVisitor<String> {

    @Override
    public String visit(AdditionNode node) {
        return "Add " + visit(node.getLeft()) + " to " + visit(node.getRight());
    }

    @Override
    public String visit(SubtractionNode node) {
        return "Subtract " + visit(node.getLeft()) + " from " + visit(node.getRight());
    }

    @Override
    public String visit(MultiplicationNode node) {
        return "Multiply " + visit(node.getLeft()) + " by " + visit(node.getRight());
    }

    @Override
    public String visit(DivisionNode node) {
        return "Divide " + visit(node.getLeft()) + " by " + visit(node.getRight());
    }

    @Override
    public String visit(NegateNode node) {
        return "negate " + visit(node.getInnerExpression());
    }

    @Override
    public String visit(FunctionNode node) {
        return "call function with arguments " + visit(node.getArgument());
    }

    @Override
    public String visit(NumberNode node) {
        return String.valueOf(node.getValue());
    }
}
