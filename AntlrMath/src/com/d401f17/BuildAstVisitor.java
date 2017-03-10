package com.d401f17;

/**
 * Created by mathias on 3/9/17.
 */
public class BuildAstVisitor extends MathBaseVisitor<ExpressionNode> {

    @Override
    public ExpressionNode visitCompileUnit(MathParser.CompileUnitContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public ExpressionNode visitNumberExpr(MathParser.NumberExprContext ctx) {
        NumberNode n = new NumberNode();
        n.setValue(Double.parseDouble(ctx.getText()));
        return n;
    }

    @Override
    public ExpressionNode visitParensExpr(MathParser.ParensExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public ExpressionNode visitInfixExpr(MathParser.InfixExprContext ctx) {
        InfixExpressionNode node;
        switch (ctx.op.getType()) {
            case MathLexer.OP_ADD:
                node = new AdditionNode();
                break;
            case MathLexer.OP_SUB:
                node = new SubtractionNode();
                break;
            case MathLexer.OP_MUL:
                node = new MultiplicationNode();
                break;
            case MathLexer.OP_DIV:
                node = new DivisionNode();
                break;
            default:
                node = new AdditionNode();
        }
        node.setLeft(visit(ctx.left));
        node.setRight(visit(ctx.right));

        return node;
    }

    @Override
    public ExpressionNode visitUnaryExpr(MathParser.UnaryExprContext ctx) {
        switch (ctx.op.getType()) {
            case MathLexer.OP_ADD:
                return visit(ctx.expr());
            case MathLexer.OP_SUB:
                NegateNode n = new NegateNode();
                n.setInnerExpression(visit(ctx.expr()));
                return n;
            default:
                return null;
        }
    }

    @Override
    public ExpressionNode visitFuncExpr(MathParser.FuncExprContext ctx) {
        FunctionNode node = new FunctionNode();
        node.setArgument(visit(ctx.expr()));
        return node;
    }
}
