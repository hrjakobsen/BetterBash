package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeException;

/**
 * Created by mathias on 4/4/17.
 */
public interface ASTVisitor<T> {
    T visit(AdditionNode node) throws TypeException;
    T visit(AndNode node) throws TypeException;
    T visit(ArithmeticExpressionNode node) throws TypeException;
    T visit(ArrayAccessNode node) throws TypeException;
    T visit(ArrayBuilderNode node) throws TypeException;
    T visit(ArrayElementAssignmentNode node) throws TypeException;
    T visit(AssignmentNode node) throws TypeException;
    T visit(AST node) throws TypeException;
    T visit(CompoundIdentifierNode node) throws TypeException;
    T visit(ConstantNode node) throws TypeException;
    T visit(DivisionNode node) throws TypeException;
    T visit(EqualsNode node) throws TypeException;
    T visit(ForkNode node) throws TypeException;
    T visit(ForNode node) throws TypeException;
    T visit(FunctionCallNode node) throws TypeException;
    T visit(FunctionNode node) throws TypeException;
    T visit(GreaterThanNode node) throws TypeException;
    T visit(GreaterThanOrEqualNode node) throws TypeException;
    T visit(IfNode node) throws TypeException;
    T visit(InfixExpressionNode node) throws TypeException;
    T visit(LessThanNode node) throws TypeException;
    T visit(LessThanOrEqualNode node) throws TypeException;
    T visit(ModuloNode node) throws TypeException;
    T visit(MultiplicationNode node) throws TypeException;
    T visit(NegationNode node) throws TypeException;
    T visit(NotEqualsNode node) throws TypeException;
    T visit(OrNode node) throws TypeException;
    T visit(ReadFromChannelNode node) throws TypeException;
    T visit(RecordDeclarationNode node) throws TypeException;
    T visit(ReturnNode node) throws TypeException;
    T visit(ShellNode node) throws TypeException;
    T visit(ShellToChannelNode node) throws TypeException;
    T visit(SimpleIdentifierNode node) throws TypeException;
    T visit(StatementsNode node) throws TypeException;
    T visit(SubtractionNode node) throws TypeException;
    T visit(TypeNode node) throws TypeException;
    T visit(VariableDeclarationNode node) throws TypeException;
    T visit(WhileNode node) throws TypeException;
    T visit(ProcedureCallNode node) throws TypeException;
    T visit(WriteToChannelNode node) throws TypeException;
    T visit(PatternMatchNode node) throws TypeException;
}
