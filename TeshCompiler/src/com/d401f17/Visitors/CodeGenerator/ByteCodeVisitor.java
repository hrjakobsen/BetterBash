package com.d401f17.Visitors.CodeGenerator;

import com.d401f17.AST.Nodes.*;
import com.d401f17.TypeSystem.*;
import com.d401f17.Visitors.BaseVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/5/17.
 */
@SuppressWarnings("Duplicates")
public class ByteCodeVisitor extends BaseVisitor<Void> {
    private ClassWriter cw = new ClassWriter(0);
    MethodVisitor mv = null;
    private SymbolTable symtab = new SymbolTable();
    private int nextAddress = 0;
    public ByteCodeVisitor() throws IOException {
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
        //TODO Rewrite to use lists


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
            this.emitLoad(s.getType(), s.getAddress()); //Push list ref

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
        mv.visitLdcInsn(node.getValue());
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
        } else {
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
        return null;
    }

    @Override
    public Void visit(FunctionNode node) {
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
            mv.visitVarInsn(ASTORE, address);
        } else if (type instanceof CharType) {
            mv.visitVarInsn(ASTORE, address);
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
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        } else if (type instanceof FloatType) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        } else if (type instanceof StringType) {
        } else if (type instanceof CharType) {
        } else if (type instanceof BoolType) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else {
            System.out.println("something was not right");
        }
    }

    private void emitNops(int n) {
        for (int i = 0; i<n; i++) {
            mv.visitInsn(NOP);
        }
        return;
    }
}
