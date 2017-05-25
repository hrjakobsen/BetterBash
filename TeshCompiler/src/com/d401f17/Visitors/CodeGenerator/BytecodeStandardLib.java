package com.d401f17.Visitors.CodeGenerator;


import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by mathias on 5/10/17.
 */
public final class BytecodeStandardLib {
    public static void Print(MethodVisitor mv) {
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitInsn(SWAP);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

    public static void CharToString(MethodVisitor mv) {
        //mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(C)Ljava/lang/String", false);
    }

    public static void FloatToString(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
    }

    public static void IntToString(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
    }

    public static void BoolToString(MethodVisitor mv) {
        Label f = new Label();
        Label done = new Label();
        mv.visitJumpInsn(IFEQ, f);
        mv.visitLdcInsn("true");
        mv.visitJumpInsn(GOTO, done);
        mv.visitLabel(f);
        mv.visitLdcInsn("false");
        mv.visitLabel(done);
    }
}
