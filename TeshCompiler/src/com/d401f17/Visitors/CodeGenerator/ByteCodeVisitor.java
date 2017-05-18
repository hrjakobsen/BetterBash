package com.d401f17.Visitors.CodeGenerator;

import com.d401f17.AST.Nodes.*;
import com.d401f17.SymbolTable.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;

import java.util.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/5/17.
 */

public class ByteCodeVisitor extends BaseVisitor<Void> {
    private List<ClassDescriptor> otherClasses = new ArrayList<>();
    private HashMap<String, String> standardFunctions= new HashMap<>();
    private ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    MethodVisitor mv = null;
    private SymbolTable symtab = new SymbolTable();
    private int nextAddress = 0;
    public ByteCodeVisitor() throws IOException {
        standardFunctions.put("print", "(Ljava/lang/String;)V");
        standardFunctions.put("intToStr", "(J)Ljava/lang/String;");
        //Set up main class
        cw.visit(52,
                ACC_PUBLIC + ACC_STATIC,
                "Main",
                null,
                "java/lang/Object",
                null);


        //Set up main method
        mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );

        mv.visitTypeInsn(NEW, "RecursiveSymbolTable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "RecursiveSymbolTable", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 0);
    }

    public List<ClassDescriptor> getOtherClasses() {
        return otherClasses;
    }

    public ClassWriter getCw() {
        return cw;
    }

    @Override
    public Void visit(AdditionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LADD);
        } else if (node.getType() instanceof FloatType) {
            mv.visitInsn(DADD);
        } else {
            //String operations
        }
        return null;
    }

    @Override
    public Void visit(AndNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        mv.visitInsn(IMUL);
        return null;
    }

    @Override
    public Void visit(ArrayAppendNode node) {
        try {
            //Get array ref
            Symbol s = symtab.lookup(node.getVariable().getName());
            //emitLoad(s.getType(), s.getAddress()); //push array ref

            

        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {

        try {
            //Get Array ref
            Symbol s = symtab.lookup(node.getArray().getName());
            //this.emitLoad(s.getType(), s.getAddress());//Push array ref

            //Get ref to innermost array
            for (int i = 0; i < node.getIndices().size(); i++) {
                node.getIndices().get(i).accept(this); //push index
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false);//pop ref; pop index; push value
            }
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }



        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {
        //Make a new list object
        mv.visitTypeInsn(NEW, "java/util/ArrayList"); //Push list ref
        //TODO: Shall a new array be initialized by some magic method?

        //Add elements to the list
        for (ArithmeticExpressionNode n : node.getValue()) {
            mv.visitInsn(DUP); //Push list ref
            n.accept(this); //Push value to add to the list
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false); //Add to the list. Push null return value
            mv.visitInsn(POP); //Pop and discard return value
        }
        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        this.emitNops(4);

        //Get list ref
        try {
            //Get list ref
            Symbol s = symtab.lookup(node.getElement().getArray().getName());
            //this.emitLoad(node.gets.getType()); //Push list ref

            //Traverse list refs to get ref to innermost array
            int i = 0;
            while (i < node.getElement().getIndices().size()) {
                node.getElement().getIndices().get(i).accept(this); //Push index
            }


            //Invoke setter

            //Clean stack

        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        this.emitNops(4);

        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        ArithmeticExpressionNode child = node.getExpression();
        child.accept(this);
        try {
            Symbol s = symtab.lookup(node.variable.getName());
            emitStore(node.getVariable().getName(), s.getType(), 0);
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Void visit(AST node) {
        return null;
    }

    @Override
    public Void visit(LiteralNode node) {
        return null;
    }

    @Override
    public Void visit(IntLiteralNode node) {
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(BoolLiteralNode node) {
        if (node.getValue()) {
            mv.visitLdcInsn(1);
        } else {
            mv.visitLdcInsn(0);
        }
        return null;
    }

    @Override
    public Void visit(FloatLiteralNode node) {
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(StringLiteralNode node) {
        mv.visitLdcInsn(node.getValue());
        return null;
    }

    @Override
    public Void visit(CharLiteralNode node) {
        mv.visitLdcInsn(Character.toString(node.getValue()));
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
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LDIV);
        } else {
            mv.visitInsn(DDIV);
        }
        return null;
    }

    @Override
    public Void visit(EqualNode node) {
        if (node.getLeft().getType() instanceof FloatType) {
            compareNumerals(IF_ICMPEQ, node);
        } else if (node.getLeft().getType() instanceof StringType){
            node.getLeft().accept(this);
            node.getRight().accept(this);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
            //TODO: COMPARE STRINGS OR CHARS
        }
        return null;
    }

    @Override
    public Void visit(ForkNode node) {
        ClassWriter innerClass = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
        ClassWriter mainClass = cw;
        cw = innerClass;
        MethodVisitor mainMethod = mv;

        String className = "fork";

        innerClass.visit(52, ACC_PUBLIC, className, null, "java/lang/Thread", null);

        innerClass.visitField(ACC_PRIVATE, "table", "LRecursiveSymbolTable;", null, null);

        //Create constructor that accepts a SymbolTable
        MethodVisitor constructor = innerClass.visitMethod(ACC_PUBLIC, "<init>", "(LRecursiveSymbolTable;)V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Thread", "<init>", "()V", false);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitVarInsn(ALOAD, 1);
        constructor.visitFieldInsn(PUTFIELD, className, "table", "LRecursiveSymbolTable;");
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(0,0);
        constructor.visitEnd();

        //implement the interface
        mv = innerClass.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, className, "table", "LRecursiveSymbolTable;");
        mv.visitVarInsn(ASTORE, 0);

        node.getChild().accept(this);

        //Return back to method that used "fork"
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        innerClass.visitEnd();

        otherClasses.add(new ClassDescriptor(className, innerClass));

        cw = mainClass;
        mv = mainMethod;

        //Create new instance of class and call fork
        mv.visitTypeInsn(NEW, className);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "(LRecursiveSymbolTable;)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "start", "()V", false);

        return null;
    }

    @Override
    public Void visit(ForNode node) { //TODO: Test this when arrays work
        //Push array ref to top of stack
        node.getArray().accept(this);
        mv.visitVarInsn(ASTORE, 3);

        Label evaluate = new Label();
        Label execute = new Label();

        //Array Index
        mv.visitLdcInsn(0);
        mv.visitVarInsn(ISTORE, 4);

        //Begin while loop
        mv.visitJumpInsn(GOTO, evaluate);
        mv.visitLabel(execute);

        //Get symbol table of function
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "openScope", "()V", false);

        //Bind name to array value
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(node.getVariable().getName());
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);

        node.getStatements().accept(this);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "closeScope", "()V", false);

        mv.visitLabel(evaluate);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "size", "()I", false);
        mv.visitJumpInsn(IF_ICMPLT, execute);
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        String funcName = node.getName().getName();


        if (standardFunctions.containsKey(funcName)) {
            for (ArithmeticExpressionNode arg : node.getArguments()) {
                arg.accept(this);
            }
            mv.visitMethodInsn(INVOKESTATIC, "StdFunc", funcName, standardFunctions.get(funcName), false);
            return null;
        }

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(funcName);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);
        mv.visitTypeInsn(CHECKCAST, "RecursiveSymbolTable");
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "clone", "()LRecursiveSymbolTable;", false);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "openScope", "()V", false);
        mv.visitVarInsn(ASTORE, 1);

        FunctionNode f = null;
        try {
            f = (FunctionNode) (symtab.lookup(funcName)).getDeclarationNode();
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        //We want to visit the node using this functions symbol table, but bind them in the new functions symbol table
        List<ArithmeticExpressionNode> arguments = node.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            ArithmeticExpressionNode argument = arguments.get(i);
            argument.accept(this);
            emitStore(f.getFormalArguments().get(i).getName().getName(), argument.getType(), 1);
        }

        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, "Main", node.getName().getName(), "(LRecursiveSymbolTable;)" + toJavaType(node.getType()), false);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "closeScope", "()V", false);

        //Restore the old symtab pointer
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ASTORE, 0);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        //Get the current symbol table
        mv.visitVarInsn(ALOAD, 0);

        //Create the new symbol table for the function
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "clone", "()LRecursiveSymbolTable;", false);

        //Order the stack: {'REC', 'func name', 'REC', 'REC', }
        mv.visitInsn(DUP);
        mv.visitInsn(DUP);
        mv.visitLdcInsn(node.getName().getName());
        mv.visitInsn(SWAP);

        //Insert the new table into itself
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
        //(Stack is now: {'REC'})

        //Load the original table
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(SWAP);
        mv.visitLdcInsn(node.getName().getName());
        mv.visitInsn(SWAP);

        //Stack is now: {'REC', "func name', 'OLD'}

        //Insert the new table into that as well
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "insert", "(Ljava/lang/String;Ljava/lang/Object;)V", false);

        //Create the snapshot of the symbol table at declaration time
        SymbolTable functionTable = new SymbolTable(symtab);
        functionTable.openScope();

        //Use the snapshot in the function to determine bindings
        SymbolTable old = symtab;
        symtab = functionTable;

        String funcName = node.getName().getName();
        Type funcType = node.getType();

        FunctionType function = new FunctionType(funcName, null, funcType);

        try {
            //Insert the function into the old symbol table
            FunctionSymbol f = new FunctionSymbol(function, node, new SymbolTable(functionTable));
            f.getSymbolTable().insert(funcName, f);
            old.insert(funcName, f);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }

        //Create the new static method
        MethodVisitor oldMv = mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, node.getName().getName(), "(LRecursiveSymbolTable;)" + toJavaType(node.getStatements().getType()), null, null);

        //Generate the method itself
        node.getStatements().accept(this);

        //if void it may not have an explicit return statement. Now add it
        if (node.getStatements().getType() instanceof OkType || node.getStatements().getType() instanceof VoidType) {
            mv.visitInsn(RETURN);
        }

        mv.visitMaxs(0,0);

        mv.visitEnd();

        //Restore the symbol table and method to main
        symtab = old;
        mv = oldMv;

        return null;
    }


    @Override
    public Void visit(GreaterThanNode node) {
        compareNumerals(IF_ICMPGT, node);
        return null;
    }

    private void compareNumerals(int cmp, InfixExpressionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        Label True = new Label();
        Label False = new Label();
        Label done = new Label();
        if (node.getLeft().getType() instanceof IntType && node.getRight().getType() instanceof IntType) {
            mv.visitInsn(LCMP);
        } else {
            mv.visitInsn(DCMPG);
        }
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(cmp, True);
        mv.visitLabel(False);
        mv.visitInsn(ICONST_0);
        mv.visitJumpInsn(GOTO, done);
        mv.visitLabel(True);
        mv.visitInsn(ICONST_1);
        mv.visitLabel(done);

    }

    @Override
    public Void visit(GreaterThanOrEqualNode node) {
        compareNumerals(IF_ICMPGE, node);
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        node.getPredicate().accept(this);
        Label falseBranch = new Label();
        Label exit = new Label();
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPNE, falseBranch);
        symtab.openScope();
        node.getTrueBranch().accept(this);
        symtab.closeScope();
        mv.visitJumpInsn(GOTO, exit);
        mv.visitLabel(falseBranch);
        symtab.openScope();
        node.getFalseBranch().accept(this);
        symtab.closeScope();
        mv.visitLabel(exit);
        return null;
    }

    @Override
    public Void visit(LessThanNode node) {
        compareNumerals(IF_ICMPLT, node);
        return null;
    }

    @Override
    public Void visit(LessThanOrEqualNode node) {
        compareNumerals(IF_ICMPLE, node);
        return null;
    }

    @Override
    public Void visit(ModuloNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        mv.visitInsn(LREM);
        return null;
    }

    @Override
    public Void visit(MultiplicationNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LMUL);
        } else {
            mv.visitInsn(DMUL);
        }
        return null;
    }

    @Override
    public Void visit(NegationNode node) {
        mv.visitInsn(ICONST_M1);
        mv.visitInsn(IMUL);
        return null;
    }

    @Override
    public Void visit(NotEqualNode node) {
        compareNumerals(IF_ICMPNE, node);
        return null;
    }

    @Override
    public Void visit(OrNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        mv.visitInsn(IADD);
        mv.visitInsn(ICONST_0);
        Label l = new Label();
        Label l2 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, l2);
        mv.visitLabel(l);
        mv.visitInsn(POP);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l2);

        return null;
    }

    @Override
    public Void visit(RecordDeclarationNode node) {
        return null;
    }

    @Override
    public Void visit(RecordIdentifierNode node) {
        return null;
    }

    @Override
    public Void visit(ReturnNode node) {
        if (node.getExpresssion() != null) {
            node.getExpresssion().accept(this);
        }

        if (node.getType() instanceof VoidType) {
            mv.visitInsn(RETURN);
        } else if (node.getType() instanceof IntType) {
            mv.visitInsn(LRETURN);
        } else if (node.getType() instanceof FloatType) {
            mv.visitInsn(DRETURN);
        } else {
            mv.visitInsn(ARETURN);
        }

        return null;
    }

    @Override
    public Void visit(ShellNode node) {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
        node.getCommand().accept(this);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Runtime", "exec", "(Ljava/lang/String;)Ljava/lang/Process;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Process", "waitFor", "()I", false);
        mv.visitInsn(POP);
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        emitLoad(node.getName(), node.getType());

        return null;
    }

    @Override
    public Void visit(StatementsNode node) {
        for (StatementNode child : node.getChildren()) {
            child.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(SubtractionNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        if (node.getType() instanceof IntType) {
            mv.visitInsn(LSUB);
        } else {
            mv.visitInsn(DSUB);
        }
        return null;
    }

    @Override
    public Void visit(TypeNode node) {
        return null;
    }

    @Override
    public Void visit(VariableDeclarationNode node) {
        Symbol s = new Symbol(node.getTypeNode().getType(), null);
        s.setAddress(getVariable());
        try {
            symtab.insert(node.getName().getName(), s);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        Label head = new Label();
        Label body = new Label();
        mv.visitJumpInsn(GOTO, head);
        mv.visitLabel(body);
        symtab.openScope();
        node.getStatements().accept(this);
        symtab.closeScope();
         mv.visitLabel(head);
        node.getPredicate().accept(this);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPEQ, body);
        return null;
    }

    @Override
    public Void visit(ProcedureCallNode node) {
        FunctionCallNode n = node.ToFunction();
        n.accept(this);
        Type returnType = node.getReturnType();
        if (!(returnType instanceof VoidType || returnType instanceof OkType)) {
            //We need to remove the return element. It can be either one or two bytes
            if (returnType instanceof IntType || returnType instanceof FloatType) {
                mv.visitInsn(POP2);
            } else {
                mv.visitInsn(POP);
            }
        }
        return null;
    }

    @Override
    public Void visit(ChannelNode node) {
        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Label exit = new Label();
        Label falseBranch = new Label();

        mv.visitMethodInsn(INVOKEVIRTUAL,"java/lang/String", "matches", "(Ljava/lang/String;)Z", false);

        //Convert the boolean (Z) value to a 1/0 integer.
        mv.visitJumpInsn(IFEQ, falseBranch);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(GOTO, exit);
        mv.visitLabel(falseBranch);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(exit);

        return null;
    }

    private int getWideVariable() {
        int address = nextAddress;
        nextAddress += 2;
        return address;
    }

    private int getVariable() {
        int address = nextAddress;
        nextAddress++;
        return address;
    }

    public byte[] getBytes() {
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    private void emitStore(String name, Type type, int address) {
        //Boxing of primitive types
        if (type instanceof IntType) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (type instanceof FloatType) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (type instanceof StringType) {
        } else if (type instanceof CharType) {
        } else if (type instanceof BoolType) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        }
        else {
            System.out.println("something was not right");
        }

        //set the element in the heap
        mv.visitVarInsn(ALOAD, address);
        mv.visitInsn(SWAP);
        mv.visitLdcInsn(name);
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "change", "(Ljava/lang/String;Ljava/lang/Object;)V", false);
    }

    private void emitLoad(String name, Type type) {
        //Load the element from the heap
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(name);
        mv.visitMethodInsn(INVOKEVIRTUAL, "RecursiveSymbolTable", "lookup", "(Ljava/lang/String;)Ljava/lang/Object;", false);

        //Unboxing of primitive types
        if (type instanceof IntType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        } else if (type instanceof FloatType) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        } else if (type instanceof StringType) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        } else if (type instanceof CharType) {
        } else if (type instanceof BoolType) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else {
            System.out.println("something was not right");
        }
    }

    private String byteCodeSignature(FunctionType typeNode) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");

        for (Type variable : typeNode.getArgs()) {
            sb.append(toJavaType(variable));
        }

        sb.append(")");
        Type t = typeNode.getReturnType();
        sb.append(toJavaType(typeNode.getReturnType()));
        return sb.toString();
    }

    private String toJavaType(Type variable) {
        if (variable instanceof IntType) {
            return "J";
        } else if (variable instanceof FloatType) {
            return "D";
        } else if (variable instanceof StringType || variable instanceof CharType) {
            return "Ljava/lang/String;";
        } else if (variable instanceof VoidType || variable instanceof OkType) {
            return "V";
        } else {
            System.out.println(variable);
            System.out.println("Invalid type conversion");
        }
        return null;
    }

    public void End() {
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
    }

    private void emitNops(int n) {
        for (int i = 0; i<n; i++) {
            mv.visitInsn(NOP);
        }
        return;
    }

    private void comment(String s) {
        mv.visitLdcInsn("COMMENT: " + s);
        mv.visitInsn(POP);
    }

    private void print() {
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    private void print(String s) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn(s);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        print();
    }
}
