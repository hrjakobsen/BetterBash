package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.AST.TypeSystem.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hense on 4/5/17.
 */
public class TypeCheckVisitor extends BaseVisitor<Void> {
    private ArrayList<Type> errorNodes = new ArrayList<>();
    private SymTab st;
    private SymTab rt;

    public TypeCheckVisitor(SymTab symbolTable, SymTab recordTable) {
        this.st = symbolTable;
        this.rt = recordTable;
    }

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
            if (leftType.getPrimitiveType() == Types.INT || leftType.getPrimitiveType() == Types.FLOAT || leftType.getPrimitiveType() == Types.STRING) {
                node.setType(leftType); //Int + Int = Int, Float + Float = Float, String + String = String
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
                node.setType(rightType); //Char + String = String
            } else if (rightType.getPrimitiveType() == Types.INT) {
                node.setType(leftType); //Char + Int = Char
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
                return null;
            }
            count++;
        }

        node.setType(node.getArray().getType());
        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        //node.get
        return null;
    }

    @Override
    public Void visit(ArrayConstantNode node) {
        List<ArithmeticExpressionNode> expressionNodes = node.getValue();
        Type[] expressionTypes = new Type[expressionNodes.size()];
        int errorIndex = 0;
        boolean allSameType = true;

        for (int i = 0; i < expressionNodes.size(); i++) {
            expressionNodes.get(i).accept(this);
            expressionTypes[i] = expressionNodes.get(i).getType();
            if (!expressionTypes[i].equals(expressionTypes[0])) {
                allSameType = false;
                errorIndex = i;
            }
        }

        if (allSameType) {
            node.setType(new ArrayType(Types.ARRAY, expressionTypes[0]));
        } else {
            node.setType(new Type(Types.ERROR, "Array construction on line " + node.getLine() + " failed. Type " + (errorIndex + 1) + " was " + expressionTypes[errorIndex] + " expected " + expressionTypes[0]));
        }

        //Check if expressions were ok
        if (invalidChildren(expressionTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

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
        node.setType(binaryEquality(node, "Equal node"));
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

        node.setType(new Type(Types.OK));

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

        FunctionType func = new FunctionType(node.getName().getName(), argumentTypes, Types.VOID);

        try {
            Type functionType = st.lookup(func.getSignature()).getType();
            node.setType(functionType);
        } catch (VariableNotDeclaredException e) {
            node.setType(new Type(Types.ERROR, "Function with signature " + e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(FunctionIdentifierNode node) {
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
        if (funcType.equals(statementsType) || (funcType.getPrimitiveType() == Types.VOID && statementsType.getPrimitiveType() == Types.OK)) {
            FunctionType function = new FunctionType(funcName, argumentTypes, funcType.getPrimitiveType());
            try {
                st.insert(function.getSignature(), new Symbol(function, node));
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
        node.setType(binaryComparison(node, "Greater than node"));
        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        node.setType(binaryComparison(node, "Greater than or equal node"));
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
            node.setType(new Type(Types.ERROR, "If statement on line " + node.getLine() + " expected a predicate of type bool, was " + predicateType));
            return null;
        }

        if (trueBranchType.equals(falseBranchType)) {
            node.setType(trueBranchType);
        } else {
            if (trueBranchType.getPrimitiveType() == Types.OK) {
                node.setType(falseBranchType);
            } else if (falseBranchType.getPrimitiveType() == Types.OK) {
                node.setType(trueBranchType);
            } else {
                node.setType(new Type(Types.ERROR, "If statement on line " + node.getLine() + " expected its false branch to be of type " + trueBranchType + ", was " + falseBranchType));
            }
        }
        return null;
    }

    @Override
    public Void visit(InfixExpressionNode node) {
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        node.setType(binaryComparison(node, "Less than node"));
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        node.setType(binaryComparison(node, "Less than or equal node"));
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
        node.setType(binaryEquality(node, "Not equal node"));
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        node.setType(booleanComparison(node, "Or node"));
        return null;
    }

    @Override
    public Void visit(ReadFromChannelNode node) {
        node.getChannel().accept(this);
        node.getExpression().accept(this);

        Type channelType = node.getChannel().getType();
        Type expType = node.getExpression().getType();

        if (invalidChildren(channelType, expType)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        if (channelType.getPrimitiveType() == Types.CHANNEL) {
            if (expType.getPrimitiveType() == Types.STRING) {
                node.setType(new Type(Types.OK));
            } else {
                node.setType(new Type(Types.ERROR, "Read from channel on line " + node.getLine() + " expected a string, got " + expType));
            }
        } else {
            node.setType(new Type(Types.ERROR, "Read from channel on line " + node.getLine() + " expected a channel, got " + channelType));
        }

        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        List<VariableDeclarationNode> varNodes = node.getVariables();
        String[] varNames = new String[varNodes.size()];
        Type[] varTypes = new Type[varNodes.size()];

        st.openScope();
        for (int i = 0; i < varNodes.size(); i++) {
            varNodes.get(i).accept(this);
            varNames[i] = varNodes.get(i).getName().getName();
            varTypes[i] = varNodes.get(i).getType();
        }
        st.closeScope();

        if (invalidChildren(varTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        String recordName = node.getName();
        Type record = new RecordType(recordName, varNames, varTypes);
        try {
            rt.insert(recordName, new Symbol(record, node));
            node.setType(new Type(Types.OK));
        } catch (VariableAlreadyDeclaredException e) {
            node.setType(new Type(Types.ERROR, e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node) {
        String name = node.getName();
        RecordType recordType;

        try {
            recordType = (RecordType) st.lookup(node.getName()).getType();
            node.setType(recordType);
        } catch (VariableNotDeclaredException e) {
            node.setType(new Type(Types.ERROR, e.getMessage()));
            return null;
        }

        IdentifierNode traveller = node.getChild();
        IdentifierNode previous = node;

        while (traveller != null) {
            if (traveller instanceof SimpleIdentifierNode) {
                try {
                    recordType = (RecordType) st.lookup(previous.getName()).getType();
                    traveller.setType(recordType.getMemberType(traveller.getName()));
                } catch (VariableNotDeclaredException e) {
                    node.setType(new Type(Types.ERROR, e.getMessage()));
                    return null;
                } catch (MemberNotFoundException e) {
                    node.setType(new Type(Types.ERROR, e.getMessage()));
                    return null;
                }

                previous = traveller;
                traveller = ((RecordIdentifierNode)traveller).getChild();
            } else {
                try {
                    Type terminalType = ((RecordType)previous.getType()).getMemberType(traveller.getName());

                    node.setType(terminalType);
                } catch (MemberNotFoundException e) {
                    node.setType(new Type(Types.ERROR, e.getMessage()));
                }
                return null;
            }
        }

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
            node.setType(new Type(Types.STRING)); //String
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
            node.setType(new Type(Types.ERROR, "Variable " + e.getMessage() + ", when used on line " + node.getLine()));
        }

        return null;
    }

    @Override
    public Void visit(StatementsNode node) {
        List<StatementNode> childNodes = node.getChildren();
        Type[] childTypes = new Type[childNodes.size()];

        for (int i = 0; i < childNodes.size(); i++) {
            childNodes.get(i).accept(this);
            childTypes[i] = childNodes.get(i).getType();
        }

        if (invalidChildren(childTypes)) {
            node.setType(new Type(Types.IGNORE));
            return null;
        }

        ArrayList<StatementNode> typedStatementNodes = new ArrayList<>();
        for (StatementNode statementNode : childNodes) {
            if (statementNode.getType().getPrimitiveType() != Types.OK && !(statementNode instanceof VariableDeclarationNode)) {
                typedStatementNodes.add(statementNode);
            }
        }

        //If we found any typed children, make sure they are all of same type
        if (typedStatementNodes.size() == 0) {
            node.setType(new Type(Types.OK));
            return null;
        } else {
            if (typedStatementNodes.size() > 1) {
                for (int i = 1; i < typedStatementNodes.size(); i++) {
                    if (!typedStatementNodes.get(i).getType().equals(typedStatementNodes.get(i - 1).getType())) {
                        int firstLine = typedStatementNodes.get(i - 1).getLine();
                        int lastLine = typedStatementNodes.get(i).getLine();
                        node.setType(new Type(Types.ERROR, "Return type of statements on line " + firstLine + " does not match that the return type on the statement on line " + lastLine));
                        return null;
                    }
                }
            }

            node.setType(typedStatementNodes.get(0).getType());
            return null;
        }
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

        if (varType.getPrimitiveType() == Types.RECORD) {
            try {
                varType = rt.lookup(((RecordType)varType).getName()).getType();
            } catch (VariableNotDeclaredException e) {
                node.setType(new Type(Types.ERROR, "Record " + e.getMessage()));
                return null;
            }
        }

        try {
            st.insert(varName, new Symbol(varType, node));
            node.setType(varType);
        } catch (VariableAlreadyDeclaredException e) {
            node.setType(new Type(Types.ERROR, "Variable " + e.getMessage()));
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

        FunctionType func = new FunctionType(node.getName().getName(), argumentTypes, Types.VOID);

        try {
            Type functionType = st.lookup(func.getSignature()).getType();
            node.setType(new Type(Types.OK));
        } catch (VariableNotDeclaredException e) {
            node.setType(new Type(Types.ERROR, "Function with signature " + e.getMessage()));
        }

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
            expType = implicitIntFloatStringToString(expType, "Expression node in write to channel node", node.getLine());
            if (expType.getPrimitiveType() == Types.STRING) {
                node.setType(new Type(Types.OK));
                return null;
            }
        }

        node.setType(new Type(Types.ERROR, "Identifier node in write to channel node on line " + node.getLine() +  " expected to be of type channel, was " + idType));

        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (leftType.getPrimitiveType() == Types.STRING && rightType.getPrimitiveType() == Types.STRING) {
            node.setType(new Type(Types.BOOL));
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

    private Type binaryEquality(InfixExpressionNode node, String nodeName) {
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
                    return new Type(Types.BOOL); //Char == Int, Int == Char
                }
            }
        }

        Type intToFloatResult = implicitIntToFloatCheck(leftType, rightType, nodeName, node.getLine());
        if (intToFloatResult.getPrimitiveType() == Types.INT || intToFloatResult.getPrimitiveType() == Types.FLOAT) {
            return new Type(Types.BOOL); //Int == Int, Int == Float
        } else {
            return new Type(Types.ERROR, nodeName + " at line " + node.getLine() + " expected similar types, but got " + leftType + " and " + rightType);
        }
    }

    private Type binaryComparison(InfixExpressionNode node, String nodeName) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new Type(Types.IGNORE);
        }

        if (leftType.equals(rightType)) {
            if (leftType.getPrimitiveType() == Types.STRING || leftType.getPrimitiveType() == Types.BOOL || leftType.getPrimitiveType() == Types.ARRAY || leftType.getPrimitiveType() == Types.RECORD || leftType.getPrimitiveType() == Types.FILE || leftType.getPrimitiveType() == Types.CHANNEL) {
                return new Type(Types.ERROR, nodeName + " at line " + node.getLine() + " expected comparable types, but got " + leftType + " and " + rightType);
            } else {
                return new Type(Types.BOOL);
            }
        } else {
            if (leftType.getPrimitiveType() == Types.CHAR || rightType.getPrimitiveType() == Types.CHAR) {
                if (leftType.getPrimitiveType() == Types.INT || rightType.getPrimitiveType() == Types.INT) {
                    return new Type(Types.BOOL); //Char == Int, Int == Char
                }
            }
        }

        Type intToFloatResult = implicitIntToFloatCheck(leftType, rightType, nodeName, node.getLine());
        if (intToFloatResult.getPrimitiveType() == Types.INT || intToFloatResult.getPrimitiveType() == Types.FLOAT) {
            return new Type(Types.BOOL); //Int == Int, Int == Float
        } else {
            return new Type(Types.ERROR, nodeName + " at line " + node.getLine() + " expected similar types, but got " + leftType + " and " + rightType);
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