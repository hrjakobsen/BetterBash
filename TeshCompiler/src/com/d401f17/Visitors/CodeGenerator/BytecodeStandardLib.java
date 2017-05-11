package com.d401f17.Visitors.CodeGenerator;


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

    }

    public static void FloatToString(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
    }

    public static void IntToString(MethodVisitor mv) {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(J)Ljava/lang/String;", false);
    }
}
