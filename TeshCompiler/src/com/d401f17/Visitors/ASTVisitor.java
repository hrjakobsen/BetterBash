package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;

/**
 * Created by mathias on 4/4/17.
 */
public interface ASTVisitor<T> {
    T visit(AdditionNode node);
    T visit(AndNode node);
    T visit(ArithmeticExpressionNode node);
    T visit(ArrayAccessNode node);
    T visit(ArrayBuilderNode node);
    T visit(ArrayElementAssignmentNode node);
    T visit(AssignmentNode node);
    T visit(AST node);
    T visit(CompoundIdentifierNode node);
    T visit(ConstantNode node);
    T visit(DivisionNode node);
    T visit(EqualsNode node);
    T visit(ForkNode node);
    T visit(ForNode node);
    T visit(FunctionCallNode node);
    T visit(FunctionNode node);
    T visit(GreaterThanNode node);
    T visit(GreaterThanOrEqualNode node);
    T visit(IfNode node);
    T visit(InfixExpressionNode node);
    T visit(LessThanNode node);
    T visit(LessThanOrEqualNode node);
    T visit(ModuloNode node);
    T visit(MultiplicationNode node);
    T visit(NegationNode node);
    T visit(NotEqualsNode node);
    T visit(OrNode node);
    T visit(ReadFromChannelNode node);
    T visit(RecordDeclarationNode node);
    T visit(ReturnNode node);
    T visit(ShellNode node);
    T visit(ShellToChannelNode node);
    T visit(SimpleIdentifierNode node);
    T visit(StatementsNode node);
    T visit(SubtractionNode node);
    T visit(TypeNode node);
    T visit(VariableDeclarationNode node);
    T visit(WhileNode node);
    T visit(ProcedureCallNode node);
    T visit(WriteToChannelNode node);
    T visit(PatternMatchNode node);
}
