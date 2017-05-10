package com.d401f17.Visitors.CodeGenerator;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.function.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/5/17.
 */

public class ByteCodeVisitor extends BaseVisitor<Void> {
    private HashMap<String, Consumer<MethodVisitor>> standardFunctions= new HashMap<>();
    private ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    MethodVisitor mv = null;
    private SymbolTable symtab = new SymbolTable();
    private int nextAddress = 0;
    public ByteCodeVisitor() throws IOException {
        standardFunctions.put("print(STRING)", BytecodeStandardLib::Print);
        standardFunctions.put("str(FLOAT)", BytecodeStandardLib::FloatToString);
        standardFunctions.put("str(INT)", BytecodeStandardLib::IntToString);
        standardFunctions.put("str(CHAR)", BytecodeStandardLib::CharToString);
        //Set up main class
        cw.visit(52,
                ACC_PUBLIC + ACC_STATIC,
                "Main",
                null,
                "java/lang/Object",
                null);
        //Create the heap field
        FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "heap","Ljava/util/ArrayList;", "Ljava/util/ArrayList<Ljava/lang/Object;>;", null);
        fv.visitEnd();

        //Set up main method
        mv = cw.visitMethod(
                ACC_PUBLIC + ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );
        //Initialize heap
        mv.visitTypeInsn(NEW, "java/util/ArrayList");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
        mv.visitFieldInsn(PUTSTATIC, "Main", "heap", "Ljava/util/ArrayList;");
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
        return null;
    }

    @Override
    public Void visit(ArrayAccessNode node) {
        try {
            Symbol s = symtab.lookup(node.getArray().getName());
            this.emitLoad(s.getType(), s.getAddress()); //Push arrayref
            for (ArithmeticExpressionNode n : node.getIndices()) {
                n.accept(this); //Push index
                mv.visitInsn(AALOAD); //arrayref, index -> value; load value from array
            }
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Void visit(ArrayBuilderNode node) {
        //this.emitNop();
        
        return null;
    }

    @Override
    public Void visit(ArrayLiteralNode node) {


        return null;
    }

    @Override
    public Void visit(ArrayElementAssignmentNode node) {
        try {
            int elements = node.getElement().getIndices().size()-1; //Number of elements in the array index
            Symbol s = symtab.lookup(node.getElement().getArray().getName());
            this.emitLoad(s.getType(), s.getAddress());    //push arrayref

            //Iterate through all reference but the last one
            //This will leave a ref to the inner array on the stack
            for (int i = 0; i < elements; i++) {
                node.getElement().getIndices().get(i).accept(this); //Push index
                mv.visitInsn(AALOAD); //arrayref, index -> value; load value from array
            }

            node.getElement().getIndices().get(elements).accept(this); //Push index
            node.getExpression().accept(this); //Push value to assign
            mv.visitInsn(AASTORE);//arrayref, index, value -> ; store value in array
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        ArithmeticExpressionNode child = node.getExpression();
        child.accept(this);
        try {
            Symbol s = symtab.lookup(node.variable.getName());
            emitStore(s.getType(), s.getAddress());
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
        return null;
    }

    @Override
    public Void visit(ForNode node) {
        return null;
    }

    @Override
    public Void visit(FunctionCallNode node) {
        List<ArithmeticExpressionNode> arguments = node.getArguments();
        Type[] argumentTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            argumentTypes[i] = arguments.get(i).getType();
        }

        String funcName = node.getName().getName();
        Type funcType = node.getType();

        FunctionType function = new FunctionType(funcName, argumentTypes, funcType);

        for (ArithmeticExpressionNode argument : node.getArguments()) {
            argument.accept(this);
        }

        if (standardFunctions.containsKey(function.getSignature())) {
            standardFunctions.get(function.getSignature()).accept(mv);
            return null;
        }

        mv.visitMethodInsn(INVOKESTATIC, "Main", node.getName().getName(), byteCodeSignature(function), false);
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
        //Create the snapshot of the symbol table at declaration time
        SymbolTable functionTable = new SymbolTable(symtab);
        functionTable.openScope();

        //Use the snapshot in the function to determine bindings
        SymbolTable old = symtab;
        symtab = functionTable;

        //Find all argument types to build functiontype
        List<VariableDeclarationNode> arguments = node.getFormalArguments();
        Type[] argumentTypes = new Type[arguments.size()];
        for (int i = 0; i < arguments.size(); i++) {
            argumentTypes[i] = arguments.get(i).getTypeNode().getType();
        }

        String funcName = node.getName().getName();
        Type funcType = node.getType();

        FunctionType function = new FunctionType(funcName, argumentTypes, funcType);

        try {
            //Insert the function into the old symbol table
            FunctionSymbol f = new FunctionSymbol(function, node, new SymbolTable(functionTable));
            f.getSymbolTable().insert(function.getSignature(), f);
            old.insert(function.getSignature(), f);
        } catch (VariableAlreadyDeclaredException e) {
            e.printStackTrace();
        }

        //Create the new static method
        MethodVisitor oldMv = mv;
        mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, node.getName().getName(), byteCodeSignature(function), null, null);

        //Allocate space in the heap for arguments
        for (int i = 0; i < arguments.size(); i++) {
            VariableDeclarationNode argument = arguments.get(i);
            argument.accept(this);
            try {
                Symbol s = symtab.lookup(argument.getName().getName());
                mv.visitVarInsn(LLOAD, i);
                emitStore(argument.getTypeNode().getType(), s.getAddress());
            } catch (VariableNotDeclaredException e) {
                e.printStackTrace();
            }
        }


        //Generate the method itself
        node.getStatements().accept(this);

        //if void it may not have a specific return statement. Now add it
        if (node.getType() instanceof VoidType) {
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
        return null;
    }

    @Override
    public Void visit(ShellToChannelNode node) {
        return null;
    }

    @Override
    public Void visit(SimpleIdentifierNode node) {
        try {
            Symbol s = symtab.lookup(node.getName());
            emitLoad(s.getType(), s.getAddress());
        } catch (VariableNotDeclaredException e) {
            e.printStackTrace();
        }
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
            //Add a new element to the heap
            mv.visitFieldInsn(GETSTATIC, "Main", "heap", "Ljava/util/ArrayList;");
            mv.visitInsn(ACONST_NULL);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z", false);
            mv.visitInsn(POP);
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
        node.ToFunction().accept(this);
        //mv.visitInsn(POP);
        return null;
    }

    @Override
    public Void visit(ChannelNode node) {
        return null;
    }

    @Override
    public Void visit(PatternMatchNode node) {
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

    private int emitStore(Type type) {
        return emitStore(type, getWideVariable());
    }

    private int emitStore(Type type, int address) {
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
        mv.visitFieldInsn(GETSTATIC, "Main", "heap", "Ljava/util/ArrayList;");
        mv.visitInsn(SWAP);
        mv.visitLdcInsn(address);
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", false);
        mv.visitInsn(POP);

        return address;
    }

    private void emitLoad(Type type, int address) {
        //Load the element from the heap
        mv.visitFieldInsn(GETSTATIC, "Main", "heap", "Ljava/util/ArrayList;");
        mv.visitLdcInsn(address);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "get", "(I)Ljava/lang/Object;", false);

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

    private void emitNop() {
        mv.visitInsn(NOP);
    }
}
