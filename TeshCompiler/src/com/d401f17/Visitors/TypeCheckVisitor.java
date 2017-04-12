package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hense on 4/5/17.
 */
public class TypeCheckVisitor extends BaseVisitor<Void> {
    private SymTab st = new SymbolTable();
    private ArrayList<Type> errorNodes = new ArrayList<>();

    public List<Type> getErrorNodes() {
        return errorNodes;
    }

    public List<String> getErrors() {
        ArrayList<String> errorStrings = new ArrayList<>();
        for (Type errorType : errorNodes) {
            errorStrings.add(errorType.getErrorMessage());
        }

        return errorStrings;
    }

    public String getAllErrors() {
        StringBuilder sb = new StringBuilder();
        for (String error : getErrors()) {
            sb.append(error);
            sb.append('\n');
        }

        return sb.toString();
    }

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
                node.setType(new Type(Types.ERROR, "Addition node on line " + node.getLine() +  " expected int, float or string, got " + leftType));
            }

            return null;
        }

        if (leftType.getPrimitiveType() == Types.INT) {
            if (rightType.getPrimitiveType() == Types.STRING || rightType.getPrimitiveType() == Types.CHAR) {
                node.setType(rightType); //String or char
            } else {
                node.setType(implicitIntToFloatCheck(leftType, rightType, "Addition node", node.getLine())); //Int or float
            }
        } else if (leftType.getPrimitiveType() == Types.FLOAT) {
            if (rightType.getPrimitiveType() == Types.STRING) {
                node.setType(rightType); //String
            } else {
                node.setType(implicitIntToFloatCheck(leftType, rightType, "Addition node", node.getLine())); //Int or float
            }
        } else if (leftType.getPrimitiveType() == Types.STRING) {
            if (rightType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.FLOAT || rightType.getPrimitiveType() == Types.CHAR || rightType.getPrimitiveType() == Types.BOOL) {
                node.setType(leftType); //String
            } else {
                node.setType(new Type(Types.ERROR, "Addition node on line " + node.getLine() +  " expected int, float, string, char or bool got " + rightType));
            }
        } else if (leftType.getPrimitiveType() == Types.CHAR) {
            if (rightType.getPrimitiveType() == Types.STRING) {
                node.setType(rightType); //String
            } else if (rightType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.CHAR) {
                node.setType(leftType); //Char
            } else {
                node.setType(new Type(Types.ERROR, "Addition node on line " + node.getLine() +  " expected int, float, string, char or bool got " + rightType));
            }
        } else if (leftType.getPrimitiveType() == Types.BOOL) {
            if (rightType.getPrimitiveType() == Types.STRING) {
                node.setType(rightType); //String
            } else {
                node.setType(new Type(Types.ERROR, "Addition node on line " + node.getLine() +  " expected int, float, string, char or bool got " + rightType));
            }
        } else {
            node.setType(new Type(Types.ERROR, "Addition node on line " + node.getLine() +  " expected int, float, string, char or bool got " + leftType));
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
                node.setType(new Type(Types.ERROR, "Index " + count + " on line " + node.getLine() +  " was expected to have type int, was " + indexType));
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

        node.setType(assignment(varType, expType, node.getLine()));

        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        node.getVariable().accept(this);
        node.getExpression().accept(this);

        Type varType = node.getVariable().getType();
        Type expType = node.getExpression().getType();

        node.setType(assignment(varType, expType, node.getLine()));

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
    public Void visit(ConstantNode node) { return null; }

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
                node.setType(new Type(Types.ERROR, "Division node on line " + node.getLine() +  " expected int or float, got " + leftType));
            }
            return null;
        }

        if (leftType.getPrimitiveType() == Types.INT) {
            if (rightType.getPrimitiveType() == Types.FLOAT) {
                node.setType(new Type(Types.FLOAT));
            } else {
                node.setType(new Type(Types.ERROR, "Right node in division node on line " + node.getLine() +  " expected to be of type int or float, was " + rightType));
            }
        } else if (leftType.getPrimitiveType() == Types.FLOAT) {
            if (rightType.getPrimitiveType() == Types.INT) {
                node.setType(new Type(Types.FLOAT));
            } else {
                node.setType(new Type(Types.ERROR, "Right node in division node on line " + node.getLine() +  " expected to be of type int or float, was " + rightType));
            }
        } else {
            node.setType(new Type(Types.ERROR, "Left node in division node on line " + node.getLine() +  " expected to be of type int or float, was " + leftType));
        }

        return null;
    }

    @Override
    public Void visit(EqualNode node) {
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
        st.openScope();

        node.getVariable().accept(this);
        node.getArray().accept(this);
        node.getStatements().accept(this);

        Type variableType = node.getVariable().getType();
        Type arrayType = node.getArray().getType();
        Type statementsType = node.getStatements().getType();

        st.closeScope();

        if (invalidChildren(variableType, arrayType, statementsType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (variableType.getPrimitiveType() != arrayType.getPrimitiveType()) {
            node.setType(new Type(Types.ERROR, "For node on line " + node.getLine() +  " expected variable of type " + arrayType + ", was " + variableType));
            return null;
        }

        node.setType(statementsType);
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        List<ArithmeticExpressionNode> arguments = node.getArguments();
        Type[] argumentTypes = new Type[arguments.size()];

        //Visit argument nodes
        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this);
            argumentTypes[i] = arguments.get(i).getType();
        }

        //Check if arguments were ok
        if (invalidChildren(argumentTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        //Visit identifier node
        node.getName().accept(this);

        Type functionType = node.getName().getType();

        //Check if identifier node is ok
        if (invalidChildren(functionType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        node.setType(functionType);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        st.openScope();

        //Visit argument nodes
        List<VariableDeclarationNode> arguments = node.getFormalArguments();
        Type[] argumentTypes = new Type[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this);
            argumentTypes[i] = arguments.get(i).getType();
        }

        //Visit statements
        node.getStatements().accept(this);

        //Close scope before there is a chance of returning from method
        st.closeScope();

        //Check if arguments were ok
        if (invalidChildren(argumentTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        //Check if statements were ok
        Type statementsType = node.getStatements().getType();
        if (invalidChildren(statementsType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        String funcName = node.getName().getName();
        Type funcType = node.getTypeNode().getType();

        //If return is same type as function, create it in the symbol table
        if (funcType.getPrimitiveType() == Types.VOID && statementsType.getPrimitiveType() == Types.OK) {
            try {
                st.insert(funcName, new Symbol(new FunctionType(funcType.getPrimitiveType(), argumentTypes), node));
                node.setType(funcType);
            } catch (VariableAlreadyDeclaredException e) {
                node.setType(new Type(Types.ERROR, e.getMessage()));
            }
        } else {
            node.setType(new Type(Types.ERROR, "Return type of function on line " + node.getLine() +  " expected to be " + funcType + ", was " + statementsType));
        }

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
            node.setType(new Type(Types.ERROR, "If node on line " + node.getLine() + " expected predicate to be of type bool, was " + predicateType));
            return null;
        }

        if (trueBranchType.equals(falseBranchType)) {
            node.setType(trueBranchType);
        } else {
            node.setType(new Type(Types.ERROR, "If node on line " + node.getLine() + " expected false branch to be of type " + trueBranchType + ", was " + falseBranchType));
        }
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
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (leftType.getPrimitiveType() == Types.INT && rightType.getPrimitiveType() == Types.INT) {
            node.setType(leftType);//Int
        } else {
            node.setType(new Type(Types.ERROR, "Both children of modulo node on line " + node.getLine() +  " expected to be of type int, was " + leftType + " and " + rightType));
        }

        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) {
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
                node.setType(leftType); //Int or float
                return null;
            } else {
                node.setType(new Type(Types.ERROR, "Multiplication node on line " + node.getLine() +  " expected int or float, got " + leftType));
                return null;
            }
        }

        node.setType(implicitIntToFloatCheck(leftType, rightType, "Multiplication node", node.getLine()));
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
            node.setType(new Type(Types.ERROR, "Negation node on line " + node.getLine() +  " expected to be of type bool, was " + expressionType));
        } else {
            node.setType(expressionType);
        }

        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
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
        node.getExpresssion().accept(this);

        Type expType = node.getExpresssion().getType();
        if (invalidChildren(expType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        node.setType(expType);

        return null;
    }

    @Override
    public Void visit(ShellNode node) {
        node.getCommand().accept(this);

        Type commandType = node.getCommand().getType();

        if (invalidChildren(commandType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (commandType.getPrimitiveType() == Types.STRING) {
            node.setType(commandType); //String
        } else {
            node.setType(new Type(Types.ERROR, "Shell node on line " + node.getLine() +  " expected to be of type string, was " + commandType));
        }

        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        node.getChannel().accept(this);
        node.getCommand().accept(this);

        Type channelType = node.getChannel().getType();
        Type commandType = node.getCommand().getType();

        if (invalidChildren(channelType, commandType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (channelType.getPrimitiveType() == Types.CHANNEL && commandType.getPrimitiveType() == Types.STRING) {
            node.setType(commandType); //String
        } else {
            if (channelType.getPrimitiveType() == Types.CHANNEL) {
                node.setType(new Type(Types.ERROR, "Shell to channel node on line " + node.getLine() + " expected command to be of type string, was " + commandType));
            } else {
                node.setType(new Type(Types.ERROR, "Shell to channel node on line " + node.getLine() +  " expected channel to be of type channel, was " + channelType));
            }
        }

        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        try {
            Type identifierType = st.lookup(node.getName()).getType();
            node.setType(identifierType);
        } catch (VariableNotDeclaredException e) {
            node.setType(new Type(Types.ERROR, e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(StatementsNode node) {
        ArrayList<StatementNode> childNodes = node.getChildren();
        Type[] childTypes = new Type[childNodes.size()];

        for (int i = 0; i < childNodes.size(); i++) {
            childNodes.get(i).accept(this);
            childTypes[i] = childNodes.get(i).getType();
        }

        if (invalidChildren(childTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        //Collect all typed children
        ArrayList<StatementNode> typedStatementNodes = new ArrayList<>();
        for (StatementNode statementNode : childNodes) {
            if (statementNode instanceof TypedStatementNode) {
                typedStatementNodes.add(statementNode);
            }
        }

        //If we found any typed children, make sure they are all of same type
        if (typedStatementNodes.size() > 1) {
            for (int i = 1; i < typedStatementNodes.size(); i++) {
                if (!typedStatementNodes.get(i).getType().equals(typedStatementNodes.get(i - 1).getType())) {
                    int firstLine = typedStatementNodes.get(i - 1).getLine();
                    int lastLine = typedStatementNodes.get(i).getLine();
                    node.setType(new Type(Types.ERROR, "Return types of statements on lines " + firstLine + " and " + lastLine + " do not match"));
                    return null;
                }
            }
        } else if (typedStatementNodes.size() == 0) {
            node.setType(new Type(Types.OK));
            return null;
        }

        node.setType(typedStatementNodes.get(0).getType());
        return null;
    }

    @Override
    public Void visit(SubtractionNode node) {
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
                node.setType(leftType); //Int or float
                return null;
            } else {
                node.setType(new Type(Types.ERROR, "Subtraction node on line " + node.getLine() +  " expected int or float, got " + leftType));
                return null;
            }
        }

        if (leftType.getPrimitiveType() == Types.CHAR && rightType.getPrimitiveType() == Types.INT) {
            node.setType(leftType); //Char
            return null;
        }

        node.setType(implicitIntToFloatCheck(leftType, rightType, "Subtraction node", node.getLine()));
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        String varName = node.getName().getName();
        Type varType = node.getTypeNode().getType();

        try {
            st.insert(varName, new Symbol(varType, node));
            node.setType(varType);
        } catch (VariableAlreadyDeclaredException e) {
            node.setType(new Type(Types.ERROR, e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        st.openScope();

        node.getPredicate().accept(this);
        node.getStatements().accept(this);

        Type predicateType = node.getPredicate().getType();
        Type statementsType = node.getStatements().getType();

        st.closeScope();

        if (invalidChildren(predicateType, statementsType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (predicateType.getPrimitiveType() != Types.BOOL) {
            node.setType(new Type(Types.ERROR, "While node on line " + node.getLine() +  " expected predicate to be of type bool, was " + predicateType));
        } else {
            node.setType(statementsType);
        }

        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        List<ArithmeticExpressionNode> arguments = node.getArguments();
        Type[] argumentTypes = new Type[arguments.size()];

        //Visit argument nodes
        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this);
            argumentTypes[i] = arguments.get(i).getType();
        }

        //Check if arguments were ok
        if (invalidChildren(argumentTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        //Visit identifier node
        node.getName().accept(this);

        Type procedureType = node.getName().getType();

        //Check if identifier node is ok
        if (invalidChildren(procedureType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        node.setType(new Type(Types.OK));
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
            node.setType(implicitIntFloatStringToString(expType, "Expression node in write to channel node", node.getLine()));
        } else {
            node.setType(new Type(Types.ERROR, "Identifier node in write to channel node on line " + node.getLine() +  " expected to be of type channel, was " + idType));
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
            node.setType(new Type(Types.ERROR, "Pattern matching node on line " + node.getLine() +  " expected both children to be of type string, was " + leftType + " and " + rightType));
        }

        return null;
    }

    private boolean invalidChildren(Type ... childTypes) {
        boolean invalid = false;

        for(Type childType : childTypes) {
            Types primitiveType = childType.getPrimitiveType();
            if (primitiveType == Types.ERROR) {
                errorNodes.add(childType);
                invalid = true;
            } else if (primitiveType == Types.IGNORE) {
                invalid = true;
            }
        }

        return invalid;
    }

    private Type assignment(Type var, Type exp, int lineNum) {
        if (invalidChildren(var, exp)) {
            return new Type(Types.IGNORE);
        }

        if (var.equals(exp)) {
            return new Type(Types.OK);
        }

        if (exp.getPrimitiveType() == Types.INT) {
            if (var.getPrimitiveType() == Types.FLOAT || var.getPrimitiveType() == Types.CHAR) {
                return new Type(Types.OK);
            }
        }

        return new Type(Types.ERROR, "Assignment on line " + lineNum + " expected expression to be of type " + var + ", was " + exp);
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
            return new Type(Types.BOOL);
        } else {
            if (leftType.getPrimitiveType() == Types.CHAR || rightType.getPrimitiveType() == Types.CHAR) {
                if (leftType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.INT) {
                    return new Type(Types.BOOL);
                }
            }
        }

        Type intToFloatResult = implicitIntToFloatCheck(leftType, rightType, nodeName, node.getLine());
        if (intToFloatResult.getPrimitiveType() == Types.INT || intToFloatResult.getPrimitiveType() == Types.FLOAT) {
            return new Type(Types.BOOL);
        } else {
            return new Type(Types.ERROR, nodeName + " at line " + node.getLine() + " expected similar types, but got " + leftType + " and " + rightType);
        }
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
                return new Type(Types.BOOL); //Bool
            } else {
                return new Type(Types.ERROR, nodeName + " on line " + node.getLine() +  " expected int or float, got " + leftType);
            }
        }

        Type implicitIntToFloatCast = implicitIntToFloatCheck(leftType, rightType, nodeName, node.getLine());
        if (implicitIntToFloatCast.getPrimitiveType() == Types.INT || implicitIntToFloatCast.getPrimitiveType() == Types.FLOAT) {
            return new Type(Types.BOOL); //Bool
        } else {
            return new Type(Types.ERROR, nodeName + " on line " + node.getLine() +  " expected int or float, got " + leftType + " and " + rightType);
        }
    }

    private Type implicitIntToFloatCheck(Type left, Type right, String nodeName, int lineNum) {
        if (left.getPrimitiveType() == Types.INT) {
            if (right.getPrimitiveType() == Types.INT || right.getPrimitiveType() == Types.FLOAT) {
                return right;//Int or float
            } else {
                return new Type(Types.ERROR, "Right node in " + nodeName + " on line " + lineNum +  " expected to be of type int or float, was " + right);
            }
        } else if (left.getPrimitiveType() == Types.FLOAT) {
            if (right.getPrimitiveType() == Types.INT || right.getPrimitiveType() == Types.FLOAT) {
                return left;//Float
            } else {
                return new Type(Types.ERROR, "Right node in " + nodeName + " on line " + lineNum +  " expected to be of type int or float, was " + right);
            }
        } else {
            return new Type(Types.ERROR, "Left node in " + nodeName + " on line " + lineNum +  " expected to be of type int or float, was " + left);
        }
    }

    private Type implicitIntFloatStringToString(Type t, String nodeName, int lineNum) {
        if (t.getPrimitiveType() == Types.INT || t.getPrimitiveType() == Types.FLOAT || t.getPrimitiveType() == Types.STRING) {
            return new Type(Types.STRING);
        } else {
            return new Type(Types.ERROR, nodeName + " on line " + lineNum +  " expected to be of type int, float or string, was " + t);
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
            return new Type(Types.ERROR, "Left node in " + nodeName + " on line " + node.getLine() +  " expected bool, got " + leftType);
        }

        if (rightType.getPrimitiveType() != Types.BOOL) {
            return new Type(Types.ERROR, "Right node in " + nodeName + " on line " + node.getLine() +  " expected bool, got " + rightType);
        }

        return new Type(Types.BOOL);
    }
}