package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;
import com.d401f17.AST.TypeSystem.TypeException;

/**
 * Created by hense on 4/5/17.
 */
public class TypeCheckVisitor extends BaseVisitor<Void> {
    //private SymbolTable st = new SymbolTable();

    @Override
    public Void visit(AdditionNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT && leftType.getPrimitiveType() != Types.STRING) {
                throw new TypeException("Addition node expected int, float or string, got " + leftType);
            }
            node.setType(leftType);

            return null;
        }

        if (leftType.getPrimitiveType() == Types.STRING) {
            if (rightType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.FLOAT || rightType.getPrimitiveType() == Types.STRING) {
                node.setType(leftType);
            } else {
                throw new TypeException("Right node expected to be of type int, float or string, was " + rightType);
            }
        } else {
            node.setType(implicitIntToFloat(leftType, rightType));
        }

        return null;
    }

    @Override
    public Void visit(AndNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        node.setType(booleanExpression(leftType, rightType));
        return null;
    }

    @Override
    public Void visit(ArithmeticExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        return null;
    }

    @Override
    public Void visit(AST node) {
        return null;
    }

    @Override
    public Void visit(CompoundIdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(ConstantNode node) {
        return null;
    }

    @Override
    public Void visit(DivisionNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Division node expected int or float, got " + leftType);
            }
            node.setType(leftType);
            return null;
        }

        if (leftType.getPrimitiveType() == Types.INT) {
            if (rightType.getPrimitiveType() == Types.INT) {
                node.setType(leftType);
            } else if (rightType.getPrimitiveType() == Types.FLOAT) {
                node.setType(rightType);
            } else {
                throw new TypeException("Right node expected to be of type int or float, was " + rightType);
            }
        } else if (leftType.getPrimitiveType() == Types.FLOAT) {
            if (rightType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.FLOAT) {
                node.setType(leftType);
            } else {
                throw new TypeException("Right node expected to be of type int or float, was " + rightType);
            }
        } else {
            throw new TypeException("Left node expected to be of type int or float, was " + leftType);
        }

        return null;
    }

    @Override
    public Void visit(EqualsNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            node.setType(leftType);
            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));

        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        return null;
    }

    @Override
    public Void visit(ForNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        return null;
    }

    @Override
    public Void visit(GreaterThanNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Greater than node expected int or float, got " + leftType);
            }
            node.setType(leftType);
            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));

        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Greater than or equal node expected int or float, got " + leftType);
            }
            node.setType(leftType);
            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));

        return null;
    }

    @Override
    public Void visit(IfNode node) throws TypeException {
        node.getPredicate().accept(this);
        node.getTrueBranch().accept(this);
        node.getFalseBranch().accept(this);

        Type predicateType = node.getPredicate().getType();
        Type trueBranchType = node.getTrueBranch().getType();
        Type falseBranchType = node.getFalseBranch().getType();

        if (predicateType.getPrimitiveType() != Types.BOOL) {
            throw new TypeException("Predicate expected bool, got " + predicateType);
        }

        if (!trueBranchType.equals(falseBranchType)) {
            throw new TypeException("False-branch expected " + trueBranchType + ", got " + falseBranchType);
        }

        node.setType(trueBranchType);
        return null;
    }

    @Override
    public Void visit(InfixExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(LessThanNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Less than node expected int or float, got " + leftType);
            }
            node.setType(leftType);
            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));

        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Less than or equal node expected int or float, got " + leftType);
            }
            node.setType(leftType);
            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));

        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Multiplication node expected int or float, got " + leftType);
            }
            node.setType(leftType);

            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        node.getExpression().accept(this);

        Type expressionType = node.getExpression().getType();

        node.setType(expressionType);

        return null;
    }

    @Override
    public Void visit(NotEqualsNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            node.setType(leftType);
            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));

        return null;
    }

    @Override
    public Void visit(OrNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        node.setType(booleanExpression(leftType, rightType));
        return null;
    }

    @Override
    public Void visit(ReadFromChannelNode node) {
        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        return null;
    }

    @Override
    public Void visit(ShellNode node) {
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(StatementsNode node) {
        return null;
    }

    @Override
    public Void visit(SubtractionNode node) throws TypeException {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() != Types.INT && leftType.getPrimitiveType() != Types.FLOAT) {
                throw new TypeException("Subtraction node expected int or float, got " + leftType);
            }
            node.setType(leftType);

            return null;
        }

        node.setType(implicitIntToFloat(leftType, rightType));
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        return null;
    }

    @Override
    public Void visit(WriteToChannelNode node) {
        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        return null;
    }

    private Type implicitIntToFloat(Type left, Type right) throws TypeException {
        if (left.getPrimitiveType() == Types.INT) {
            if (right.getPrimitiveType() == Types.INT || right.getPrimitiveType() == Types.FLOAT) {
                return right;
            } else {
                throw new TypeException("Right node expected to be of type int or float, was " + right);
            }
        } else if (left.getPrimitiveType() == Types.FLOAT) {
            if (right.getPrimitiveType() == Types.INT || right.getPrimitiveType() == Types.FLOAT) {
                return left;
            } else {
                throw new TypeException("Right node expected to be of type int or float, was " + right);
            }
        } else {
            throw new TypeException("Left node expected to be of type int or float, was " + left);
        }
    }

    private Type booleanExpression(Type left, Type right) throws TypeException {
        if (left.getPrimitiveType() != Types.BOOL) {
            throw new TypeException("Left node expected bool, got " + left);
        }

        if (right.getPrimitiveType() != Types.BOOL) {
            throw new TypeException("Right node expected bool, got " + right);
        }

        return left;
    }
}
