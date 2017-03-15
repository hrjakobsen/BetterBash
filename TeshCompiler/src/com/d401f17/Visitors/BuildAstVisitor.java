package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TeshBaseVisitor;
import com.d401f17.TeshParser;
import com.d401f17.TeshVisitor;

/**
 * Created by mathias on 3/15/17.
 */
public class BuildAstVisitor extends TeshBaseVisitor<AST> {
    @Override
    public AST visitCompileUnit(TeshParser.CompileUnitContext ctx) {
        return visit(ctx.statement());
    }

    @Override
    public AST visitWriteToChannelStatement(TeshParser.WriteToChannelStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitEmptyStatement(TeshParser.EmptyStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitAssignmentStatement(TeshParser.AssignmentStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitCompoundStatement(TeshParser.CompoundStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitForStatement(TeshParser.ForStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitIfStatement(TeshParser.IfStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitReturnStatement(TeshParser.ReturnStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitVariableDelcarationStatement(TeshParser.VariableDelcarationStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitRecordDeclarationStatement(TeshParser.RecordDeclarationStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitVarStatement(TeshParser.VarStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitWhileStatement(TeshParser.WhileStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitReadFromChannelStatement(TeshParser.ReadFromChannelStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitChannelDeclarationStatement(TeshParser.ChannelDeclarationStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitArrayBuilderStatement(TeshParser.ArrayBuilderStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitFunctionCallStatement(TeshParser.FunctionCallStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitMultiStatement(TeshParser.MultiStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitFunctionDeclarationStatement(TeshParser.FunctionDeclarationStatementContext ctx) {
        return null;
    }

    @Override
    public AST visitExpression(TeshParser.ExpressionContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleComparison(TeshParser.SingleComparisonContext ctx) {
        return null;
    }

    @Override
    public AST visitLogicalComparison(TeshParser.LogicalComparisonContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleArithmeticExpr(TeshParser.SingleArithmeticExprContext ctx) {
        return null;
    }

    @Override
    public AST visitBoolComparison(TeshParser.BoolComparisonContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleTerm(TeshParser.SingleTermContext ctx) {
        return null;
    }

    @Override
    public AST visitArithmeticExpr(TeshParser.ArithmeticExprContext ctx) {
        return null;
    }

    @Override
    public AST visitTermExpr(TeshParser.TermExprContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleValue(TeshParser.SingleValueContext ctx) {
        return null;
    }

    @Override
    public AST visitUnaryOperator(TeshParser.UnaryOperatorContext ctx) {
        return null;
    }

    @Override
    public AST visitNegateOperator(TeshParser.NegateOperatorContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleFinal(TeshParser.SingleFinalContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleConstant(TeshParser.SingleConstantContext ctx) {
        return null;
    }

    @Override
    public AST visitSingleIdentifier(TeshParser.SingleIdentifierContext ctx) {
        return null;
    }

    @Override
    public AST visitFunctionCallExpr(TeshParser.FunctionCallExprContext ctx) {
        return null;
    }

    @Override
    public AST visitArrayAccessExpr(TeshParser.ArrayAccessExprContext ctx) {
        return null;
    }

    @Override
    public AST visitParenthesisExpr(TeshParser.ParenthesisExprContext ctx) {
        return null;
    }

    @Override
    public AST visitIdentifier(TeshParser.IdentifierContext ctx) {
        return null;
    }

    @Override
    public AST visitRecordDeclaration(TeshParser.RecordDeclarationContext ctx) {
        return null;
    }

    @Override
    public AST visitVariableDelcaration(TeshParser.VariableDelcarationContext ctx) {
        return null;
    }

    @Override
    public AST visitFunctionDeclaration(TeshParser.FunctionDeclarationContext ctx) {
        return null;
    }

    @Override
    public AST visitChannelDeclaration(TeshParser.ChannelDeclarationContext ctx) {
        return null;
    }

    @Override
    public AST visitArrayAccess(TeshParser.ArrayAccessContext ctx) {
        return null;
    }

    @Override
    public AST visitConstant(TeshParser.ConstantContext ctx) {
        return null;
    }

    @Override
    public AST visitType(TeshParser.TypeContext ctx) {
        return null;
    }
}