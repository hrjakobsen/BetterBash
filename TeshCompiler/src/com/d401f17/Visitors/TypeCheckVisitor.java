package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.Type;
import com.d401f17.AST.TypeSystem.Types;

import java.util.ArrayList;

/**
 * Created by hense on 4/5/17.
 */
public class TypeCheckVisitor extends BaseVisitor<Void> {
    //private SymTab st = new SymbolTable();
    private ArrayList<Type> errors = new ArrayList<>();

    @Override
    public Void visit(AdditionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() == Types.INT || leftType.getPrimitiveType() == Types.FLOAT || leftType.getPrimitiveType() == Types.STRING || leftType.getPrimitiveType() == Types.CHAR) {
                node.setType(leftType); //Int, float, string or char
            } else {
                node.setType(new Type(Types.ERROR, "Addition node expected int, float or string, got " + leftType));
            }

            return null;
        }

        if (leftType.getPrimitiveType() == Types.INT) {
            if (rightType.getPrimitiveType() == Types.STRING || rightType.getPrimitiveType() == Types.CHAR) {
                node.setType(rightType); //String or char
            } else {
                node.setType(implicitIntToFloat(leftType, rightType, "Addition node"));
            }
        } else if (leftType.getPrimitiveType() == Types.FLOAT) {
            if (rightType.getPrimitiveType() == Types.STRING) {
                node.setType(new Type(Types.STRING)); //String
            } else {
                node.setType(implicitIntToFloat(leftType, rightType, "Addition node"));
            }
        } else if (leftType.getPrimitiveType() == Types.STRING) {
            if (rightType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.FLOAT || rightType.getPrimitiveType() == Types.CHAR || rightType.getPrimitiveType() == Types.BOOL) {
                node.setType(rightType); //Int, float, char or bool
            } else {
                node.setType(new Type(Types.ERROR, "Addition node expected int, float, string, char or bool got " + rightType));
            }
        } else if (leftType.getPrimitiveType() == Types.BOOL) {
            if (rightType.getPrimitiveType() == Types.STRING) {
                node.setType(new Type(Types.STRING)); //String
            } else {
                node.setType(new Type(Types.ERROR, "Addition node expected int, float, string, char or bool got " + rightType));
            }
        } else {
            node.setType(new Type(Types.ERROR, "Addition node expected int, float, string, char or bool got " + leftType));
        }

        return null;
    }

    @Override
    public Void visit(AndNode node) {
        node.setType(booleanComparison(node, "And node"));
        return null;
    }

    @Override
    public Void visit(ArithmeticExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        int count = 0;

        for(ArithmeticExpressionNode index : node.getIndices()) {
            index.accept(this);

            Type indexType = index.getType();

            if (invalidChildren(indexType)) {
                node.setType(new Type(Types.IGNORE));
                return null;
            }

            if (indexType.getPrimitiveType() != Types.INT) {
                node.setType(new Type(Types.ERROR, "Index " + count + " was expected to have type int, was " + indexType));
            }
            count++;
        }

        node.setType(node.getArray().getType());
        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        node.getElement().accept(this);
        node.getExpression().accept(this);

        Type varType = node.getElement().getType();
        Type expType = node.getExpression().getType();

        node.setType(assignment(varType, expType));

        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        node.getVariable().accept(this);
        node.getExpression().accept(this);

        Type varType = node.getVariable().getType();
        Type expType = node.getExpression().getType();

        node.setType(assignment(varType, expType));

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
        node.accept(this);
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() == Types.INT || leftType.getPrimitiveType() == Types.FLOAT) {
                node.setType(leftType);
            } else {
                node.setType(new Type(Types.ERROR, "Division node expected int or float, got " + leftType));
            }
            return null;
        }

        if (leftType.getPrimitiveType() == Types.INT) {
            if (rightType.getPrimitiveType() == Types.FLOAT) {
                node.setType(new Type(Types.FLOAT));
            } else {
                node.setType(new Type(Types.ERROR, "Right node in division node expected to be of type int or float, was " + rightType));
            }
        } else if (leftType.getPrimitiveType() == Types.FLOAT) {
            if (rightType.getPrimitiveType() == Types.INT) {
                node.setType(new Type(Types.FLOAT));
            } else {
                node.setType(new Type(Types.ERROR, "Right node in division node expected to be of type int or float, was " + rightType));
            }
        } else {
            node.setType(new Type(Types.ERROR, "Left node in division node expected to be of type int or float, was " + leftType));
        }

        return null;
    }

    @Override
    public Void visit(EqualsNode node) {
        node.setType(equalComparison(node, "Equal node"));
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        node.getChild().accept(this);

        Type statementType = node.getChild().getType();

        if (invalidChildren(statementType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        node.setType(statementType);

        return null;
    }

    @Override
    public Void visit(ForNode node) {
        node.getVariable().accept(this);
        node.getArray().accept(this);
        node.getStatements().accept(this);

        Type variableType = node.getVariable().getType();
        Type arrayType = node.getArray().getType();
        Type statementsType = node.getStatements().getType();

        if (invalidChildren(variableType, arrayType, statementsType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (variableType.getPrimitiveType() != arrayType.getPrimitiveType()) {
            node.setType(new Type(Types.ERROR, "For node expected variable to of type " + arrayType + ", was " + variableType));
            return null;
        }

        node.setType(statementsType);
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
    public Void visit(GreaterThanNode node) {
        node.setType(binaryIntFloat(node, "Greater than node"));
        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        node.setType(binaryIntFloat(node, "Greater than or equal node"));
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        node.getPredicate().accept(this);
        node.getTrueBranch().accept(this);
        node.getFalseBranch().accept(this);

        Type predicateType = node.getPredicate().getType();
        Type trueBranchType = node.getTrueBranch().getType();
        Type falseBranchType = node.getFalseBranch().getType();

        if (invalidChildren(predicateType, trueBranchType, falseBranchType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (predicateType.getPrimitiveType() != Types.BOOL) {
            node.setType(new Type(Types.ERROR, "If node expected predicate to be of type bool, was " + predicateType));
        }

        if (!trueBranchType.equals(falseBranchType)) {
            node.setType(new Type(Types.ERROR, "If node expected false branch to be of type " + trueBranchType + ", was " + falseBranchType));
        }

        node.setType(trueBranchType);
        return null;
    }

    @Override
    public Void visit(InfixExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        node.setType(binaryIntFloat(node, "Less than node"));
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        node.setType(binaryIntFloat(node, "Less than or equal node"));
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) {
        node.setType(binaryIntFloat(node, "Multiplication node"));
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        node.getExpression().accept(this);

        Type expressionType = node.getExpression().getType();

        if (invalidChildren(expressionType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (expressionType.getPrimitiveType() != Types.BOOL) {
            node.setType(new Type(Types.ERROR, "Negation node expected to be of type bool, was " + expressionType));
        }

        node.setType(expressionType);

        return null;
    }

    @Override
    public Void visit(NotEqualsNode node) {
        node.setType(equalComparison(node, "Not equal node"));
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        node.setType(booleanComparison(node, "Or node"));
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
    public Void visit(SubtractionNode node) {
        node.setType(binaryIntFloat(node, "Subtraction node"));
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
        node.getPredicate().accept(this);
        node.getStatements().accept(this);

        Type predicateType = node.getPredicate().getType();
        Type statementsType = node.getStatements().getType();

        if (invalidChildren(predicateType, statementsType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (predicateType.getPrimitiveType() != Types.BOOL) {
            node.setType(new Type(Types.ERROR, "While node expected predicate to be of type bool, was " + predicateType));
        }

        node.setType(statementsType);
        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        node.getName().accept(this);
        return null;
    }

    @Override
    public Void visit(WriteToChannelNode node) {
        node.getIdentifier().accept(this);
        node.getExpression().accept(this);

        Type idType = node.getIdentifier().getType();
        Type expType = node.getExpression().getType();

        if (invalidChildren(idType, expType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (idType.getPrimitiveType() == Types.CHANNEL) {
            node.setType(implicitIntFloatStringToString(expType, "Expression node in write to channel node"));
        } else {
            node.setType(new Type(Types.ERROR, "Identifier node in write to channel node expected to be of type channel, was " + idType));
        }

        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.getPrimitiveType() == Types.STRING && rightType.getPrimitiveType() == Types.STRING) {
            node.setType(new Type(Types.STRING));
        } else {
            node.setType(new Type(Types.ERROR, "Pattern matching node expected both children to be of type string, was " + leftType + " and " + rightType));
        }

        return null;
    }

    private boolean invalidChildren(Type ... childrenTypes) {
        boolean valid = true;

        for(Type childType : childrenTypes) {
            if (childType.getPrimitiveType() == Types.ERROR) {
                errors.add(childType);
                valid = false;
            } else if (childType.getPrimitiveType() == Types.IGNORE) {
                valid = false;
            }
        }

        return valid;
    }

    private Type assignment(Type var, Type exp) {
        if (invalidChildren(var, exp)) {
            return new Type(Types.IGNORE);
        }

        if (var.equals(exp)) {
            return new Type(Types.OK);
        } else {
            return new Type(Types.ERROR, "Assignment expected expression to be of type " + var + ", was " + exp);
        }
    }

    private Type equalComparison(InfixExpressionNode node, String nodeName) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new Type(Types.IGNORE);
        }

        if (leftType.equals(rightType)) {
            return leftType;
        }

        return implicitIntToFloat(leftType, rightType, nodeName);
    }

    private Type binaryIntFloat(InfixExpressionNode node, String nodeName) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new Type(Types.IGNORE);
        }

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() == Types.INT || leftType.getPrimitiveType() == Types.FLOAT) {
                return leftType;
            } else {
                return new Type(Types.ERROR, nodeName + " expected int or float, got " + leftType);
            }
        }

        return implicitIntToFloat(leftType, rightType, nodeName);
    }

    private Type implicitIntToFloat(Type left, Type right, String nodeName) {
        if (invalidChildren(left, right)) {
            return new Type(Types.IGNORE);
        }

        if (left.getPrimitiveType() == Types.INT) {
            if (right.getPrimitiveType() == Types.INT || right.getPrimitiveType() == Types.FLOAT) {
                return right;
            } else {
                return new Type(Types.ERROR, "Right node in " + nodeName + " expected to be of type int or float, was " + right);
            }
        } else if (left.getPrimitiveType() == Types.FLOAT) {
            if (right.getPrimitiveType() == Types.INT || right.getPrimitiveType() == Types.FLOAT) {
                return left;
            } else {
                return new Type(Types.ERROR, "Right node in " + nodeName + " expected to be of type int or float, was " + right);
            }
        } else {
            return new Type(Types.ERROR, "Left node in " + nodeName + " expected to be of type int or float, was " + left);
        }
    }

    private Type implicitIntFloatStringToString(Type t, String nodeName) {
        if (t.getPrimitiveType() == Types.INT || t.getPrimitiveType() == Types.FLOAT || t.getPrimitiveType() == Types.STRING) {
            return new Type(Types.STRING);
        } else {
            return new Type(Types.ERROR, nodeName + " expected to be of type int, float or string, was " + t);
        }
    }

    private Type booleanComparison(InfixExpressionNode node, String nodeName) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new Type(Types.IGNORE);
        }

        if (leftType.getPrimitiveType() != Types.BOOL) {
            return new Type(Types.ERROR, "Left node in " + nodeName + " expected bool, got " + leftType);
        }

        if (rightType.getPrimitiveType() != Types.BOOL) {
            return new Type(Types.ERROR, "Right node in " + nodeName + " expected bool, got " + rightType);
        }

        return new Type(Types.BOOL);
    }
}