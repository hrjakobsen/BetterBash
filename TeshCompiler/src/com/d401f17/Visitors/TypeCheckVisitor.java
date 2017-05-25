package com.d401f17.Visitors;

import com.d401f17.AST.Nodes.*;
import com.d401f17.Helper;
import com.d401f17.SymbolTable.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.Interpreter.StandardLib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hense on 4/5/17.
 */
public class TypeCheckVisitor extends BaseVisitor<Void> {
    private List<Type> errorNodes = new ArrayList<>();
    private SymTab st;
    private SymTab rt;

    public TypeCheckVisitor() {
        this(new SymbolTable(), new SymbolTable());
    }

    public TypeCheckVisitor(SymTab symbolTable, SymTab recordTable) {
        StandardLib.InsertFunctionNames(symbolTable);
        this.st = symbolTable;
        this.rt = recordTable;
    }

    public List<Type> getErrorNodes() {
        Collections.sort(errorNodes);
        return errorNodes;
    }

    public List<String> getErrors() {
        ArrayList<String> errorStrings = new ArrayList<>();
        for (Type errorType : getErrorNodes()) {
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
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType.equals(rightType)) {
            if (leftType instanceof FloatType || leftType instanceof StringType) {
                node.setType(leftType); //Int + Int = Int, Float + Float = Float, String + String = String
            } else {
                node.setType(new ErrorType(node.getLine(), "Expected int, float or string, got " + leftType));
            }
            return null;
        }

        if (leftType instanceof FloatType && rightType instanceof FloatType) {
            node.setType(new FloatType()); //Int + Float = Float, Float + Int = Float, Float + Float = Float
            return null;
        } else {
            if (leftType instanceof CharType || rightType instanceof CharType) {
                if (leftType instanceof IntType || rightType instanceof IntType) {
                    node.setType(new CharType()); //Int + Char = Char, Char + Int = Char
                } else {
                    node.setType(new ErrorType(node.getLine(), "Expected char and int or int and char, got " + leftType + " and " + rightType));
                }
                return null;
            }
        }

        node.setType(new ErrorType(node.getLine(), "Expected addable types, got " + leftType + " and " + rightType));
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        node.setType(booleanComparison(node));
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        node.getArray().accept(this);
        List<ArithmeticExpressionNode> indexNodes = node.getIndices();

        Type arrayType = node.getArray().getType();
        Type[] indexTypes = new Type[indexNodes.size()];

        if (invalidChildren(arrayType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (arrayType instanceof ArrayType) {
            for (int i = 0; i < indexNodes.size(); i++) {
                if (!(arrayType instanceof ArrayType)) {
                    node.setType(new ErrorType(node.getLine(),"Array has " + i + " levels, tried to access a " + Helper.ordinal(indexNodes.size()) + " level"));
                    return null;
                }
                indexNodes.get(i).accept(this);
                indexTypes[i] = indexNodes.get(i).getType();

                if (invalidChildren(indexTypes[i])) {
                    node.setType(new IgnoreType());
                    return null;
                }

                if (!(indexTypes[i] instanceof IntType)) {
                    node.setType(new ErrorType(node.getLine(), Helper.ordinal(i + 1) + " index expected int, got " + indexTypes[i]));
                    return null;
                }
                arrayType = ((ArrayType)arrayType).getChildType();
            }
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected array, got " + arrayType));
        }

        if (invalidChildren(indexTypes)) {
            node.setType(new IgnoreType());
            return null;
        }

        node.setType(arrayType);
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
        node.getVariable().accept(this);
        node.getExpression().accept(this);

        Type varType = node.getVariable().getType();
        Type expType = node.getExpression().getType();

        if (invalidChildren(varType, expType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (varType instanceof ArrayType) {
            ArrayType arrType = (ArrayType)varType;
            if (arrType.getChildType().getClass().isInstance(expType)) {
                node.setType(new OkType());
            } else {
                node.setType(new ErrorType(node.getLine(), "Expected " + arrType.getChildType() + ", got " + expType));
            }
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected array, got " + varType));
        }

        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        //Visit array identifier
        node.getArray().accept(this);

        //Get type of array
        Type arrayType = node.getArray().getType();

        if (invalidChildren(arrayType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (arrayType instanceof ArrayType) {
            //Open af new scope for the loop's body and variable
            openScope();

            //Get the type of the array
            Type varType = ((ArrayType) arrayType).getChildType();

            //Get name of variable
            String varName = node.getVariable().getName();

            //Insert the variable in the symbol table
            //Since we're in a brand new empty scope an exception can't possibly be thrown
            try {
                if (varType instanceof RecordType) {
                    RecordType r = (RecordType)varType;
                    varType = rt.lookup(r.getName()).getType();

                }
                st.insert(varName, new Symbol(varType, node));
                st.insert(varName, new Symbol(varType, node));
                node.setType(varType);
            } catch (VariableAlreadyDeclaredException e) {} catch (VariableNotDeclaredException e) {
                e.printStackTrace();
            }

            //Now that the variable has been declared, we can visit the statements
            node.getExpression().accept(this);
            Type expressionType = node.getExpression().getType();

            //Close the scope again, before there is any chance of returning from this method
            closeScope();

            if (invalidChildren(arrayType, expressionType)) {
                node.setType(new IgnoreType());
                return null;
            }

            if (expressionType instanceof BoolType) {
                node.setType(arrayType);
            } else {
                node.setType(new ErrorType(node.getLine(), "Expected a boolean expression, got " + expressionType));
            }
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected an array, got " + arrayType));
        }

        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {
        List<ArithmeticExpressionNode> expressionNodes = node.getValue();

        if (expressionNodes.isEmpty()) {
            node.setType(new ArrayType(new VoidType()));
            return null;
        }

        Type[] expressionTypes = new Type[expressionNodes.size()];
        int errorIndex = 0;
        boolean allSameType = true;

        for (int i = 0; i < expressionNodes.size(); i++) {
            expressionNodes.get(i).accept(this);
            expressionTypes[i] = expressionNodes.get(i).getType();
        }

        for (int i = 1; i < expressionNodes.size(); i++) {
            if (!expressionTypes[i].equals(expressionTypes[i - 1])) {
                errorIndex = i;
                allSameType = false;
            }
        }

        if (allSameType) {
            node.setType(new ArrayType(expressionTypes[0]));
        } else {
            node.setType(new ErrorType(node.getLine(), Helper.ordinal(errorIndex + 1) + " element expected " + expressionTypes[errorIndex - 1] + ", got " + expressionTypes[errorIndex]));
        }

        //Check if expressions were ok
        if (invalidChildren(expressionTypes)) {
            node.setType(new IgnoreType());
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
    public Void visit(LiteralNode node) { return null; }

    @Override
    public Void visit(IntLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(BoolLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(FloatLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(CharLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(RecordLiteralNode node) {
        return null;
    }

    @Override
    public Void visit(DivisionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType instanceof FloatType && rightType instanceof FloatType) {
            if (leftType instanceof IntType && rightType instanceof IntType) {
                node.setType(leftType); //Int / Int = Int
            } else {
                node.setType(new FloatType()); //Int / Float = Float, Float / Int = Float, Float / Float = Float
            }
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected int or float, got " + leftType));
        }

        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        node.setType(binaryEquality(node));
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        openScope();
        node.getChild().accept(this);
        closeScope();

        Type childType = node.getChild().getType();

        if (invalidChildren(childType)) {
            node.setType(new IgnoreType());
            return null;
        }

        node.setType(new OkType());

        return null;
    }

    @Override
    public Void visit(ForNode node) {
        //Visit array identifier
        node.getArray().accept(this);

        //Get type of array
        Type arrayType = node.getArray().getType();

        if (invalidChildren(arrayType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (arrayType instanceof ArrayType) {
            //Open af new scope for the loop's body and variable
            openScope();

            //Get the type of the array
            Type varType = ((ArrayType) arrayType).getChildType();

            //Make the variable immutable
            varType.setImmutable(true);

            //Get name of variable
            String varName = node.getVariable().getName();

            //Insert the variable in the symbol table
            //Since we're in a brand new empty scope an exception can't possibly be thrown
            try {
                if (varType instanceof RecordType) {
                    RecordType r = (RecordType)varType;
                    varType = rt.lookup(r.getName()).getType();

                }
                st.insert(varName, new Symbol(varType, node));

                node.setType(varType);
            } catch (VariableAlreadyDeclaredException e) {} catch (VariableNotDeclaredException e) {
                e.printStackTrace();
            }

            //Now that the variable has been declared, we can visit the statements
            node.getStatements().accept(this);
            Type statementsType = node.getStatements().getType();

            //Close the scope again, before there is any chance of returning from this method
            closeScope();

            if (invalidChildren(arrayType, statementsType)) {
                node.setType(new IgnoreType());
                return null;
            }

            node.setType(statementsType);
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected an array, got " + arrayType));
        }

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
            node.setType(new IgnoreType());
            return null;
        }

        FunctionType tempFunc = new FunctionType(node.getName().getName(), argumentTypes, new VoidType());

        try {
            FunctionType functionType = (FunctionType)st.lookup(node.getName().getName()).getType();
            if (tempFunc.isValidCallOf(functionType)) {
                node.setType(functionType.getReturnType());
            } else {
                node.setType(new ErrorType(node.getLine(), "Function with signature " + tempFunc.toString() + " not declared"));
            }
        } catch (VariableNotDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Function with name " + e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        //This code is run in a a dummy scope that is discarded immediately. It's sole purpose is to
        //check what types the arguments have before visiting the body, but we also need their
        //names to be inserted in the scope used by the body of the function.
        openScope();

        List<VariableDeclarationNode> arguments = node.getFormalArguments();
        Type[] argumentTypes = new Type[arguments.size()];

        for (int i = 0; i < arguments.size(); i++) {
            arguments.get(i).accept(this);
            argumentTypes[i] = arguments.get(i).getTypeNode().getType();
        }

        closeScope();

        //We need to insert the function into the scope in which the function is declared, before visiting
        //the body of the function anything to allow for recursion.
        String funcName = node.getName().getName();
        Type funcType = node.getTypeNode().getType();

        FunctionType function = new FunctionType(funcName, argumentTypes, funcType);
        try {
            st.insert(funcName, new Symbol(function, node));
            node.setType(funcType);
        } catch (VariableAlreadyDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Function with signature " + e.getMessage()));
            return null;
        }

        //Open the actual scope
        openScope();

        //Visit the arguments again
        for (VariableDeclarationNode argumentNode : arguments) {
            argumentNode.accept(this);
        }

        //Check if arguments were ok
        if (invalidChildren(argumentTypes)) {
            //Close scope before leaving
            closeScope();
            node.setType(new IgnoreType());
            return null;
        }

        //Visit statements
        node.getStatements().accept(this);

        closeScope();

        //Check if statements were ok
        Type statementsType = node.getStatements().getType();
        if (invalidChildren(statementsType)) {
            node.setType(new IgnoreType());
            return null;
        }

        //If return is same type as function, set type of the node to the return type of the function
        if (funcType.equals(statementsType) || (funcType instanceof VoidType && statementsType instanceof OkType)) {
            node.setType(new OkType());
        } else {
            node.setType(new ErrorType(node.getLine(), "Return expected " + funcType + ", got " + statementsType));
        }

        return null;
    }

    @Override
    public Void visit(GreaterThanNode node) {
        node.setType(binaryComparison(node));
        return null;
    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        node.setType(binaryComparison(node));
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        node.getPredicate().accept(this);

        openScope();
        node.getTrueBranch().accept(this);
        closeScope();

        openScope();
        node.getFalseBranch().accept(this);
        closeScope();

        Type predicateType = node.getPredicate().getType();
        Type trueBranchType = node.getTrueBranch().getType();
        Type falseBranchType = node.getFalseBranch().getType();

        if (invalidChildren(predicateType, trueBranchType, falseBranchType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (!(predicateType instanceof BoolType)) {
            node.setType(new ErrorType(node.getLine(), "Expected bool, got " + predicateType));
            return null;
        }

        if (trueBranchType.equals(falseBranchType)) {
            node.setType(trueBranchType);
        } else {
            if (trueBranchType instanceof OkType) {
                node.setType(falseBranchType);
            } else if (falseBranchType instanceof OkType) {
                node.setType(trueBranchType);
            } else {
                node.setType(new ErrorType(node.getLine(), "Expected false branch of type " + trueBranchType + ", got " + falseBranchType));
            }
        }
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        node.setType(binaryComparison(node));
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        node.setType(binaryComparison(node));
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType instanceof IntType && rightType instanceof IntType) {
            node.setType(leftType); //Int Mod Int = Int
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected int and int, got " + leftType + " and " + rightType));
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
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType instanceof FloatType && rightType instanceof FloatType) {
            if (leftType instanceof IntType && rightType instanceof IntType) {
                node.setType(leftType); //Int + Int = Int
            } else {
                node.setType(new FloatType()); //Int + Float = Float, Float + Int = Float
            }
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected int or float and int or float, got " + leftType + " and " + rightType));
        }

        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        node.getExpression().accept(this);

        Type expressionType = node.getExpression().getType();

        if (invalidChildren(expressionType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (expressionType instanceof BoolType) {
            node.setType(expressionType);
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected bool, got " + expressionType));
        }

        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
        node.setType(binaryEquality(node));
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        node.setType(booleanComparison(node));
        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        List<VariableDeclarationNode> varNodes = node.getVariables();
        String[] varNames = new String[varNodes.size()];
        Type[] varTypes = new Type[varNodes.size()];

        openScope();
        for (int i = 0; i < varNodes.size(); i++) {
            varNodes.get(i).accept(this);
            varNames[i] = varNodes.get(i).getName().getName();
            varTypes[i] = varNodes.get(i).getTypeNode().getType();
        }
        closeScope();

        if (invalidChildren(varTypes)) {
            node.setType(new IgnoreType());
            return null;
        }

        String recordName = node.getName();
        Type record = new RecordType(recordName, varNames, varTypes);
        try {
            rt.insert(recordName, new Symbol(record, node));
            node.setType(new OkType());
        } catch (VariableAlreadyDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Record " + e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node) {
        RecordType recordType;
        Type tempType = null;

        try {
            tempType = st.lookup(node.getName()).getType();
            recordType = (RecordType)tempType;
            node.setType(recordType);
        } catch (VariableNotDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Variable " + e.getMessage()));
            return null;
        } catch (ClassCastException e) {
            node.setType(new ErrorType(node.getLine(), "Expected first identifier to be a RECORD, got " + tempType));
            return null;
        }

        int elementNum = 1;
        IdentifierNode traveller = node.getChild();
        IdentifierNode previous = node;

        while (traveller != null) {
            if (traveller instanceof SimpleIdentifierNode) {
                try {
                    Type terminalType = ((RecordType)previous.getType()).getMemberType(traveller.getName());
                    node.setType(terminalType);
                } catch (MemberNotFoundException e) {
                    node.setType(new ErrorType(node.getLine(), e.getMessage()));
                } catch (ClassCastException e) {
                    node.setType(new ErrorType(node.getLine(), "Expected " + Helper.ordinal(elementNum) + " element to be RECORD, was " + previous.getType()));
                }
                return null;
            } else {
                try {
                    tempType = st.lookup(previous.getName()).getType();
                    recordType = (RecordType) tempType;
                    traveller.setType(recordType.getMemberType(traveller.getName()));
                } catch (VariableNotDeclaredException e) {
                    node.setType(new ErrorType(node.getLine(), "Expected " + Helper.ordinal(elementNum) + " element to be RECORD, was " + previous.getType()));
                    return null;
                } catch (MemberNotFoundException e) {
                    node.setType(new ErrorType(node.getLine(), e.getMessage()));
                    return null;
                } catch (ClassCastException e) {
                    node.setType(new ErrorType(node.getLine(), "Expected " + elementNum + " to be RECORD, was " + tempType));
                    return null;
                }

                previous = traveller;
                traveller = ((RecordIdentifierNode)traveller).getChild();
                elementNum++;
            }
        }

        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        node.getExpresssion().accept(this);

        Type expType = node.getExpresssion().getType();

        if (invalidChildren(expType)) {
            node.setType(new IgnoreType());
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
            node.setType(new IgnoreType());
            return null;
        }

        if (commandType instanceof StringType) {
            node.setType(new OkType());
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected string, got " + commandType));
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
            node.setType(new IgnoreType());
            return null;
        }

        if (channelType instanceof ChannelType && commandType instanceof OkType) {
            node.setType(new OkType());
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected channel and string, got " + channelType + " and " + commandType));
        }

        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        try {
            Type identifierType = st.lookup(node.getName()).getType();
            node.setType(identifierType);
        } catch (VariableNotDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Variable " + e.getMessage()));
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
            node.setType(new IgnoreType());
            return null;
        }

        ArrayList<StatementNode> typedStatementNodes = new ArrayList<>();
        for (StatementNode statementNode : childNodes) {
            if (!(statementNode.getType() instanceof OkType) && !(statementNode instanceof VariableDeclarationNode)) {
                typedStatementNodes.add(statementNode);
            }
        }

        //If we found any typed children, make sure they are all of same type
        if (typedStatementNodes.isEmpty()) {
            node.setType(new OkType());
            return null;
        } else {
            if (typedStatementNodes.size() > 1) {
                for (int i = 1; i < typedStatementNodes.size(); i++) {
                    if (!typedStatementNodes.get(i).getType().equals(typedStatementNodes.get(i - 1).getType())) {
                        node.setType(new ErrorType(node.getLine(), "Return types do not match"));
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
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType instanceof FloatType && rightType instanceof FloatType) {
            if (leftType instanceof IntType && rightType instanceof IntType) {
                node.setType(leftType); //Int - Int = Int
            } else {
                node.setType(new FloatType()); //Int - Float = Float, Float - Int = Float
            }
            return null;
        } else {
            if (leftType instanceof CharType && rightType instanceof IntType) {
                node.setType(leftType); //Char - Int = Char
                return null;
            }
        }

        node.setType(new ErrorType(node.getLine(), "Expected int or float and int, float or char, got " + leftType + " and " + rightType));
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

        if (varType instanceof RecordType) {
            try {
                varType = rt.lookup(((RecordType)varType).getName()).getType();
                //The typenode of a record doesn't know anything about the members of the record
                node.getTypeNode().setType(varType);
            } catch (VariableNotDeclaredException e) {
                node.setType(new ErrorType(node.getLine(), "Record " + e.getMessage()));
                node.getTypeNode().setType(new ErrorType(node.getLine(), "Record " + e.getMessage()));
                return null;
            }
        }

        try {
            st.insert(varName, new Symbol(varType, node));
            node.setType(new OkType());
        } catch (VariableAlreadyDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Variable " + e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        openScope();

        node.getPredicate().accept(this);
        node.getStatements().accept(this);

        Type predicateType = node.getPredicate().getType();
        Type statementsType = node.getStatements().getType();

        closeScope();

        if (invalidChildren(predicateType, statementsType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (predicateType instanceof BoolType) {
            node.setType(statementsType);
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected bool, got " + predicateType));
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
            node.setType(new IgnoreType());
            return null;
        }

        FunctionType tempFunc = new FunctionType(node.getName().getName(), argumentTypes, new VoidType());

        try {
            FunctionType functionType = (FunctionType)st.lookup(node.getName().getName()).getType();
            if (tempFunc.isValidCallOf(functionType)) {
                node.setType(new OkType());
                node.setReturnType(functionType.getReturnType());
            } else {
                node.setType(new ErrorType(node.getLine(), "Function with signature " + tempFunc.toString() + " not declared"));
            }
        } catch (VariableNotDeclaredException e) {
            node.setType(new ErrorType(node.getLine(), "Function with name " + e.getMessage()));
        }

        return null;
    }

    @Override
    public Void visit(ChannelNode node) {
        node.getIdentifier().accept(this);
        node.getExpression().accept(this);

        Type leftType = node.getIdentifier().getType();
        Type rightType = node.getExpression().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType instanceof ChannelType) { //Write to channel
            if (rightType instanceof StringType) {
                node.setType(new OkType());
            } else {
                node.setType(new ErrorType(node.getLine(), "Expected channel and string, got " + leftType + " and " + rightType));
            }
        } else if (rightType instanceof ChannelType) { //Read from channel
            if (leftType instanceof StringType) {
                node.setType(new OkType());
            } else {
                node.setType(new ErrorType(node.getLine(), "Expected string and channel, got " + leftType + " and " + rightType));
            }
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected channel and string or string and channel, got " + leftType + " and " + rightType));
        }

        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            node.setType(new IgnoreType());
            return null;
        }

        if (leftType instanceof StringType && rightType instanceof StringType) {
            node.setType(new BoolType());
        } else {
            node.setType(new ErrorType(node.getLine(), "Expected string and string, got " + leftType + " and " + rightType));
        }

        return null;
    }

    private boolean invalidChildren(Type ... childTypes) {
        boolean invalid = false;

        for(Type childType : childTypes) {
            if (childType instanceof ErrorType) {
                errorNodes.add(childType);
                invalid = true;
            } else if (childType instanceof IgnoreType) {
                invalid = true;
            }
        }

        return invalid;
    }

    private Type assignment(Type var, Type exp, int lineNum) {
        if (invalidChildren(var, exp)) {
            return new IgnoreType();
        }

        if (var.isImmutable()) {
            return new ErrorType(lineNum, "Attempted assignment to immutable variable");
        }

        boolean success;
        if ((var instanceof ArrayType && exp instanceof ArrayType) || (var instanceof RecordType && exp instanceof RecordType)) {
            success = exp.equals(var);
        } else {
            success = var.getClass().isInstance(exp);
        }

        if (var instanceof CharType && exp instanceof IntType || success) {
            return new OkType();
        } else {
            return new ErrorType(lineNum, "Expected " + var + ", got " + exp);
        }
    }

    private Type binaryEquality(InfixExpressionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new IgnoreType();
        }

        if (leftType instanceof FloatType && rightType instanceof FloatType) {
            return new BoolType(); //Int == Int, Int == Float, Float == Int, Float == Float
        } else if (leftType instanceof CharType && rightType instanceof CharType) {
            return new BoolType(); //Char == Char
        } else if (leftType instanceof StringType && rightType instanceof StringType) {
            return new BoolType(); //String == String
        } else if (leftType instanceof BoolType && rightType instanceof BoolType) {
            return new BoolType(); //Bool == Bool
        } else {
            return new ErrorType(node.getLine(), "Expected equalable types, got " + leftType + " and " + rightType);
        }
    }

    private Type binaryComparison(InfixExpressionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new IgnoreType();
        }

        if (leftType instanceof FloatType && rightType instanceof FloatType) {
            return new BoolType(); //Int < Int, Int < Float, Float < Int, Float < Float
        } else if (leftType instanceof  CharType && rightType instanceof CharType) {
            return new BoolType(); //Int < Char, Char < Int
        }

        return new ErrorType(node.getLine(), "Expected comparable types got " + leftType + " and " + rightType);
    }

    private Type booleanComparison(InfixExpressionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getType();
        Type rightType = node.getRight().getType();

        if (invalidChildren(leftType, rightType)) {
            return new IgnoreType();
        }

        if (!(leftType instanceof BoolType)) {
            return new ErrorType(node.getLine(), "Expected bool, got " + leftType);
        }

        if (!(rightType instanceof BoolType)) {
            return new ErrorType(node.getLine(), "Expected bool, got " + rightType);
        }

        return new BoolType();
    }

    private void openScope() {
        st.openScope();
        rt.openScope();
    }

    private void closeScope() {
        st.closeScope();
        rt.closeScope();
    }
}
